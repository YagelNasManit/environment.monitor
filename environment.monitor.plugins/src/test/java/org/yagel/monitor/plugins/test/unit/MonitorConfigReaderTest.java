package org.yagel.monitor.plugins.test.unit;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.yagel.monitor.MonitorConfig;
import org.yagel.monitor.plugins.MonitorConfigReader;

public class MonitorConfigReaderTest {

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
