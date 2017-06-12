package org.yagel.monitor;

import java.util.Map;


public interface MonitorStatusCollector {

  // synchronise?
  Map<Resource, ResourceStatus> updateStatus();
}
