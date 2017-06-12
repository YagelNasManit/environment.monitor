package org.yagel.monitor;

public interface EnvironmentMonitorJob extends Runnable {

  void removeListener(UpdateStatusListener listener);

  void addListener(UpdateStatusListener listener);
}
