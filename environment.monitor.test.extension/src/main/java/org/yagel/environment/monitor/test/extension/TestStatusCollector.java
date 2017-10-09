package org.yagel.environment.monitor.test.extension;

import org.apache.log4j.Logger;
import org.yagel.environment.monitor.test.extension.provider.AbstractResourceStatusProvider;
import org.yagel.environment.monitor.test.extension.provider.CoreApiServiceStatusProvider;
import org.yagel.environment.monitor.test.extension.provider.DataBaseStatusProvider;
import org.yagel.environment.monitor.test.extension.provider.WebUIStatusProvider;
import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.MonitorStatusCollector;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.resource.ResourceStatusImpl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TestStatusCollector implements MonitorStatusCollector {

  private static final Logger log = Logger.getLogger(TestStatusCollector.class);
  private EnvironmentConfig config;


  public TestStatusCollector(EnvironmentConfig config) {
    this.config = config;
  }


  @Override
  public Set<ResourceStatus> updateStatus() {

    Set<ResourceStatus> statusSet = new HashSet<>();


    List<AbstractResourceStatusProvider> providerList = Arrays.asList(
        new CoreApiServiceStatusProvider(config),
        new DataBaseStatusProvider(config),
        new WebUIStatusProvider(config));


    for (AbstractResourceStatusProvider provider : providerList)
      statusSet.add(provider.reloadStatus());


    for (ResourceStatus status : statusSet) {
      log.info("Environment: " + config.getEnvName()
          + " Status of resource: " + status.getResource().getId()
          + " is: " + status.getStatus()
          + " status detsils: "+ status.getStatusDetails()
      );
    }

    return statusSet;

  }

}
