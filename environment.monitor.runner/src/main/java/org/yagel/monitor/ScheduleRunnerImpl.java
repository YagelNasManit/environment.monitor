package org.yagel.monitor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.yagel.monitor.exception.ScheduleRunnerException;
import org.yagel.monitor.mongo.ResourceDAO;
import org.yagel.monitor.mongo.ResourceLastStatusDAO;
import org.yagel.monitor.mongo.ResourceStatusDetailDAO;
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
import java.util.stream.Collectors;
import javax.annotation.PreDestroy;

public class ScheduleRunnerImpl implements ScheduleRunner {

  private static Logger log = Logger.getLogger(ScheduleRunnerImpl.class);

  @Autowired
  private JarScanner jarScanner;

  @Autowired
  private ResourceDAO resourceDAO;

  @Autowired
  private ResourceLastStatusDAO lastStatusDAO;

  @Autowired
  private ResourceStatusDetailDAO statusDetailDAO;


  private MonitorStatusCollectorLoader collectorLoader;
  private ClassLoader classLoader;
  private ScheduledExecutorService executor;
  private MonitorConfig config;
  private Map<String, EnvironmentMonitorJobImpl> tasks;

  public void runTasks(ClassLoader classLoader) {
    this.classLoader = classLoader;
    this.tasks = new WeakHashMap<>();
    this.jarScanner.scanJar(this.getPluginPath(), classLoader);
    this.collectorLoader = new ProxyCollectorLoader(jarScanner.getStatusCollectorLoader());
    this.config = jarScanner.getMonitorConfig();


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
          this.shutdown();
          this.executor = null;
          this.tasks = null;
          this.runTasks(this.classLoader);
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

  @PreDestroy
  public void onDestroy() {
    System.out.println("Shutting down threads");
    shutdown();
    System.out.println("Threads down");
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

      if (Thread.interrupted()) {
        log.info("Monitor Job: " + config.getEnvName() + " is interrupted. No new verifications will be done");
        return;
      }

      try {
        this.lastUpdate = new Date();
        ScheduleRunnerImpl.this.selfDiagnostic();
        log.info("\n====>Updating status for " + config.getEnvName() + " environment.");

        Set<ResourceStatus> status = collector.updateStatus();

        lastStatusDAO.insert(config.getEnvName(), status);
        statusDetailDAO.insert(config.getEnvName(), status);
        resourceDAO.insert(status.stream().map(ResourceStatus::getResource).collect(Collectors.toSet()));
        this.updateListeners(status);

      } catch (Exception e1) {
        // task pool will try to live as much as possible, therefore will proceed
        log.error("exception on status update for " + config.getEnvName() + " environment.", e1);
      }
      catch (Error er){
        // ok, things are getting serious, notify that we have a problem (otherwise task will fail silently)
        log.error("error on status update for " + config.getEnvName() + " environment.", er);
        // and kill task
        throw er;
      }
    }

    private void updateListeners(Set<ResourceStatus> status) {
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
