package org.yagel.monitor.status.collector;

import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.MonitorStatusCollector;

public interface MonitorStatusCollectorLoader {


  MonitorStatusCollector loadCollector(EnvironmentConfig config);

}
