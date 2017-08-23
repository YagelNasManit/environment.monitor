package org.yagel.environment.monitor.test.extension.provider;

import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.Resource;
import org.yagel.monitor.ResourceStatusProvider;

public abstract class AbstractResourceStatusProvider implements ResourceStatusProvider {

  protected Resource resource;
  protected EnvironmentConfig config;
  protected int proviterVersion = 1;

  public AbstractResourceStatusProvider(EnvironmentConfig config) {
    this.config = config;
  }
}
