package org.yagel.monitor;


import java.util.Set;

public interface UpdateStatusListener {

  String getEnvName();

  void update(Set<ResourceStatus> lastChangedStatus);

  boolean isActive();
}
