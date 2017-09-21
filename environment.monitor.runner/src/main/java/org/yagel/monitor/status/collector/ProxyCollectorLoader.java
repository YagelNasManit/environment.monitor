package org.yagel.monitor.status.collector;

import org.apache.log4j.Logger;
import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.MonitorStatusCollector;


public class ProxyCollectorLoader implements MonitorStatusCollectorLoader {

  private static final Logger log = Logger.getLogger(ProxyCollectorLoader.class);
  private final MonitorStatusCollectorLoader originalLoader;

  public ProxyCollectorLoader(MonitorStatusCollectorLoader originalLoader) {
    this.originalLoader = originalLoader;
  }

  @Override
  public MonitorStatusCollector loadCollector(EnvironmentConfig config) {
    log.info("Loaded MonitorStatusCollector for env: " + config.getEnvName());
    return originalLoader.loadCollector(config);
  }
}
