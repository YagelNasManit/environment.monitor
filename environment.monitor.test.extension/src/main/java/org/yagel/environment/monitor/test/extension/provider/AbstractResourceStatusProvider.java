package org.yagel.environment.monitor.test.extension.provider;

import org.yagel.environment.monitor.test.util.StatusRandomizer;
import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.Resource;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.ResourceStatusProvider;
import org.yagel.monitor.resource.ResourceStatusImpl;
import org.yagel.monitor.resource.Status;

import java.util.Date;

public abstract class AbstractResourceStatusProvider implements ResourceStatusProvider {

  protected Resource resource;
  protected EnvironmentConfig config;
  protected int proviterVersion = 1;

  public AbstractResourceStatusProvider(EnvironmentConfig config) {
    this.config = config;
  }

  @Override
  public ResourceStatus reloadStatus() {
    Date date = new Date();
    Status status = StatusRandomizer.random();
    return new ResourceStatusImpl(
        getResource(),
        status,
        date,
        "Status check for "+ getResource().getId()+" at date: "+ date+ " finalised with status: "+ status
    );
  }
}
