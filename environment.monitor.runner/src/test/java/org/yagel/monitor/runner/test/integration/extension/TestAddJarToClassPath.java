package org.yagel.monitor.runner.test.integration.extension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.yagel.monitor.MonitorConfig;
import org.yagel.monitor.RunnerBeansConfiguration;
import org.yagel.monitor.plugins.JarScanner;
import org.yagel.monitor.status.collector.MonitorStatusCollectorLoader;

import java.io.IOException;
import javax.xml.bind.JAXBException;


/**
 * Test is meant to be run during maven build, as in other case plugin.jar.location will not be available
 */
@ContextConfiguration(classes = RunnerBeansConfiguration.class)
public class TestAddJarToClassPath extends AbstractTestNGSpringContextTests{

  @Autowired
  JarScanner jarScanner;

  @Test
  public void testLoaderIsFound() throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, JAXBException {
    String pathToLocalJar = System.getProperty("plugin.jar.location");
    jarScanner.scanJar(pathToLocalJar, ClassLoader.getSystemClassLoader());
    MonitorStatusCollectorLoader loader = jarScanner.getStatusCollectorLoader();
    Assert.assertNotNull(loader);

  }

  @Test
  public void testConfigIsFound() throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, JAXBException {
    String pathToLocalJar = System.getProperty("plugin.jar.location");
    jarScanner.scanJar(pathToLocalJar, ClassLoader.getSystemClassLoader());
    MonitorConfig config = jarScanner.getMonitorConfig();
    Assert.assertNotNull(config);

  }

}
