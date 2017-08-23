package org.yagel.monitor;

import java.util.Set;


public interface MonitorStatusCollector {

  // synchronise?
  Set<ResourceStatus> updateStatus();
}
