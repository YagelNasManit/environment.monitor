package org.yagel.monitor.plugins;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.yagel.monitor.MonitorConfig;

public class TestMonitorConfigReader {

  @Test
  void testMonitorConfigDeserialization() {

    String mockConfig = getClass().getClassLoader().getResource("Environment-Mock.xml").toString();

    MonitorConfig config = new MonitorConfigReader().readMonitorConfig(mockConfig);

    Assert.assertNotNull(config);

    Assert.assertNotNull(config.getEnvironments());

    config.getEnvironments().forEach(Assert::assertNotNull);

    config.getEnvironments().forEach(environmentConfig -> Assert.assertNotNull(environmentConfig.getCheckResources())

    );
  }
}
