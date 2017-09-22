package org.yagel.monitor.plugins.test.unit;

import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.MonitorConfig;
import org.yagel.monitor.plugins.MonitorConfigReader;
import org.yagel.monitor.plugins.PluginsBeansConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ContextConfiguration(classes = PluginsBeansConfiguration.class)
public class MonitorConfigReaderTest extends AbstractTestNGSpringContextTests{

  @Autowired
  private MonitorConfigReader reader;

  @Test
  void testMonitorConfigDeserialization() {

    String mockConfig = getClass().getClassLoader().getResource("Environment-Mock.xml").toString();

    MonitorConfig config =reader.readMonitorConfig(mockConfig);

    Assert.assertNotNull(config);
    Assert.assertNotNull(config.getEnvironments());
    config.getEnvironments().forEach(Assert::assertNotNull);
    config.getEnvironments().forEach(environmentConfig -> Assert.assertNotNull(environmentConfig.getCheckResources())
    );

    Set<String> envNames = Sets.newSet("Mock","Mock2");
    Set<String> resNames = Sets.newSet("Mock1Resource","Mock2Resource");
    long taskDelay = 60;
    int appVersion = 1;

    Map<String, String> props = new HashMap<>();
    props.put("prop1","1");
    props.put("prop2","2");

    config.getEnvironments().stream().map(EnvironmentConfig::getEnvName).forEach(envName -> Assert.assertTrue(envNames.contains(envName)));
    config.getEnvironments().stream().map(EnvironmentConfig::getCheckResources).forEach(checkRes -> Assert.assertTrue(checkRes.equals(resNames)));
    config.getEnvironments().stream().map(EnvironmentConfig::getAdditionalProperties).forEach(adProps -> Assert.assertTrue(adProps.equals(props)));
    config.getEnvironments().stream().map(EnvironmentConfig::getTaskDelay).forEach(delay -> Assert.assertTrue(taskDelay == delay));
    config.getEnvironments().stream().map(EnvironmentConfig::getAppVersion).forEach(version -> Assert.assertTrue(version == appVersion));
  }

}
