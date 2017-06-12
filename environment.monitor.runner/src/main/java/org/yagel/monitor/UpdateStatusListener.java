package org.yagel.monitor;


import java.util.Map;

public interface UpdateStatusListener {

  String getEnvName();

  void update(Map<Resource, ResourceStatus> lastChangedStatus);

  boolean isActive();
}
