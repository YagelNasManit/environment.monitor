package org.yagel.monitor;


public interface ScheduleRunner {

  void runTasks();

  void shutdown();

  void addListener(UpdateStatusListener listener);

  void removeListener(UpdateStatusListener listener);

  MonitorConfig getConfig();

}
