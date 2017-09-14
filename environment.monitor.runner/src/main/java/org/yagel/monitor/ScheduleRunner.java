package org.yagel.monitor;


public interface ScheduleRunner {

  void runTasks(ClassLoader classLoader);

  void shutdown();

  void addListener(UpdateStatusListener listener);

  void removeListener(UpdateStatusListener listener);

  MonitorConfig getConfig();

}
