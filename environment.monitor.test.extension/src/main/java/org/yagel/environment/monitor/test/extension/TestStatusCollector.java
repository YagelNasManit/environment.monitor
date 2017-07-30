package org.yagel.environment.monitor.test.extension;

import org.apache.log4j.Logger;
import org.yagel.environment.monitor.test.extension.provider.AbstractResourceStatusProvider;
import org.yagel.environment.monitor.test.extension.provider.CoreApiServiceStatusProvider;
import org.yagel.environment.monitor.test.extension.provider.DataBaseStatusProvider;
import org.yagel.environment.monitor.test.extension.provider.WebUIStatusProvider;
import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.MonitorStatusCollector;
import org.yagel.monitor.Resource;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.resource.ResourceStatusImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestStatusCollector implements MonitorStatusCollector {

  private static final Logger log = Logger.getLogger(TestStatusCollector.class);
  private EnvironmentConfig config;


  public TestStatusCollector(EnvironmentConfig config) {
    this.config = config;
  }


  @Override
  public Map<Resource, ResourceStatus> updateStatus() {

    Map<Resource, ResourceStatus> statusMap = new HashMap<>();

    List<AbstractResourceStatusProvider> providerList = Arrays.asList(
        new CoreApiServiceStatusProvider(config),
        new DataBaseStatusProvider(config),
        new WebUIStatusProvider(config));


    for (AbstractResourceStatusProvider provider : providerList)
      statusMap.put(provider.getResource(), new ResourceStatusImpl(provider.getResource().getId(), provider.reloadStatus()));


    for (Map.Entry<Resource, ResourceStatus> entry : statusMap.entrySet()) {
      log.info("Environment: " + config.getEnvName() + " Status of resource: " + entry.getKey().getId() + " is: " + entry.getValue().getStatus());
    }

    return statusMap;

  }

}
