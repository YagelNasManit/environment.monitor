package org.yagel.monitor;

import org.apache.log4j.Logger;
import org.yagel.monitor.exception.ScheduleRunnerException;
import org.yagel.monitor.mongo.MongoConnector;
import org.yagel.monitor.plugins.JarScanner;
import org.yagel.monitor.status.collector.MonitorStatusCollectorLoader;
import org.yagel.monitor.status.collector.ProxyCollectorLoader;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduleRunnerImpl implements ScheduleRunner {

  private static Logger log = Logger.getLogger(ScheduleRunnerImpl.class);
  private static ScheduleRunnerImpl scheduleRunnerImpl;

  private MonitorStatusCollectorLoader collectorLoader;
  private ClassLoader classLoader;

  private ScheduledExecutorService executor;
  private MonitorConfig config;
  private Map<String, EnvironmentMonitorJobImpl> tasks = new WeakHashMap<>();

  private ScheduleRunnerImpl(ClassLoader classLoader) {
    this.classLoader = classLoader;
    JarScanner jarScanner = new JarScanner(classLoader, this.getPluginPath());
    jarScanner.scanJar();
    this.collectorLoader = new ProxyCollectorLoader(jarScanner.getStatusCollectorLoader());
    this.config = jarScanner.getMonitorConfig();

  }

  public static ScheduleRunner getInstance() {
    if (scheduleRunnerImpl == null)
      throw new ScheduleRunnerException("Calling schedule runner before initialization, use newInstance first");
    return scheduleRunnerImpl;
  }

  public synchronized static ScheduleRunner newInstance(ClassLoader classLoader) {
    if (scheduleRunnerImpl != null) {
      scheduleRunnerImpl.shutdown();
    }
    scheduleRunnerImpl = new ScheduleRunnerImpl(classLoader);

    return getInstance();
  }

  public void runTasks() {
    this.executor = setupScheduler(config);
    log.info("Initialize tasks");


    for (EnvironmentConfig env : config.getEnvironments()) {
      EnvironmentMonitorJobImpl task = new EnvironmentMonitorJobImpl(env, collectorLoader.loadCollector(env));

      tasks.put(env.getEnvName(), task);
      executor.scheduleWithFixedDelay(task, 10, env.getTaskDelay(), TimeUnit.SECONDS);

    }

    log.info("Initialize tasks - done");
  }

  public void shutdown() {
    try {
      this.executor.shutdown();
      // todo replace with seconds 20 seconds
      this.executor.awaitTermination(40, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      this.executor.shutdownNow();
    }
  }

  @Override
  public void addListener(UpdateStatusListener listener) {
    tasks.get(listener.getEnvName()).addListener(listener);
  }

  public void removeListener(UpdateStatusListener listener) {
    tasks.get(listener.getEnvName()).removeListener(listener);
  }

  public MonitorConfig getConfig() {
    return this.config;
  }

  private ScheduledExecutorService setupScheduler(MonitorConfig config) {
    log.info("Setup Scheduler");
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(config.getEnvironments().size());
    log.info("Setup Scheduler - done");
    return executor;
  }


  private void selfDiagnostic() throws Exception {
    synchronized (tasks) {
      Calendar waitDelay = Calendar.getInstance();
      waitDelay.add(Calendar.MINUTE, -10);
      for (Map.Entry<String, EnvironmentMonitorJobImpl> task : tasks.entrySet()) {
        if (waitDelay.getTime().after(task.getValue().getUpdated())) {
          log.error("\n\n\n The " + task.getKey() + " task does not respond too long time.\n\n\n");
          log.error("\n\n Schedule service going to restart! \n\n");
          executor.shutdown();
          scheduleRunnerImpl = new ScheduleRunnerImpl(scheduleRunnerImpl.classLoader);
          throw new Exception("The " + task.getKey() + " task does not respond too long time.");
        }
      }
    }
  }

  private String getPluginPath() {
    String location = System.getProperty("plugin.jar.location");
    if (location == null)
      throw new ScheduleRunnerException("Plugin jar path should be provided, pls define system property: plugin.jar.location");

    return location;
  }


  private class EnvironmentMonitorJobImpl implements EnvironmentMonitorJob {

    private Date lastUpdate = new Date();
    private Set<UpdateStatusListener> listeners;
    private EnvironmentConfig config;
    private MonitorStatusCollector collector;


    public EnvironmentMonitorJobImpl(EnvironmentConfig config, MonitorStatusCollector collector) {
      this.listeners = Collections.synchronizedSet(new HashSet<UpdateStatusListener>());
      this.config = config;
      this.collector = collector;
    }


    @Override
    public void removeListener(UpdateStatusListener listener) {
      this.listeners.remove(listener);
    }

    @Override
    public void addListener(UpdateStatusListener listener) {
      this.listeners.add(listener);
    }

    public Date getUpdated() {
      return lastUpdate;
    }

    @Override
    public void run() {
      try {
        this.lastUpdate = new Date();
        ScheduleRunnerImpl.this.selfDiagnostic();
        log.info("\n====>Updating status for " + config.getEnvName() + " environment.");

        Map<Resource, ResourceStatus> status = collector.updateStatus();

        MongoConnector.getInstance().getLastStatusDAO().insert(config.getEnvName(), status.values());
        MongoConnector.getInstance().getMonthDetailDAO().insert(config.getEnvName(), status.values());
        MongoConnector.getInstance().getResourceDAO().insert(status.keySet());
        this.updateListeners(status);

      } catch (Exception e1) {
        log.error("exception on status update for " + config.getEnvName() + " environment.", e1);
      }
    }

    private void updateListeners(Map<Resource, ResourceStatus> status) {
      Iterator<UpdateStatusListener> iListeners = listeners.iterator();
      while (iListeners.hasNext()) {
        UpdateStatusListener listener = iListeners.next();
        try {

          if (listener.isActive()) {
            listener.update(status);
          } else {
            iListeners.remove();
          }
        } catch (Exception e) {
          log.error("update listener exception. ", e);
        }
      }
    }
  }

}
