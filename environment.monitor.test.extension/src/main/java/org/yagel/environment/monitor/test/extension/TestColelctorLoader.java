package org.yagel.environment.monitor.test.extension;

import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.MonitorStatusCollector;
import org.yagel.monitor.status.collector.MonitorStatusCollectorLoader;

public class TestColelctorLoader implements MonitorStatusCollectorLoader {

  public MonitorStatusCollector loadCollector(EnvironmentConfig environmentConfig) {

    return new TestStatusCollector(environmentConfig);

  }


}
