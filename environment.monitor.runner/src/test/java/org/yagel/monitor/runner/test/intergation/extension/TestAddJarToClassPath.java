package org.yagel.monitor.runner.test.intergation.extension;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.yagel.monitor.RunnerBeansConfiguration;
import org.yagel.monitor.plugins.JarScanner;
import org.yagel.monitor.status.collector.MonitorStatusCollectorLoader;

import java.io.IOException;
import javax.xml.bind.JAXBException;

@ContextConfiguration(classes = RunnerBeansConfiguration.class)
public class TestAddJarToClassPath extends AbstractTestNGSpringContextTests{

  @Test(enabled = false)
  public void testLoaderIsFound() throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, JAXBException {


    String pathToLocalJar = System.getProperty("plugin.jar.location");//"/Users/oleh_kovalyshyn/Self_Development/Github/EnvMonitor/source/plugin-extension/target/plugin-extension-1.0-SNAPSHOT.jar";

    JarScanner collectorFinder = new JarScanner();

    collectorFinder.scanJar(pathToLocalJar, ClassLoader.getSystemClassLoader());
    MonitorStatusCollectorLoader loader = collectorFinder.getStatusCollectorLoader();

    Assert.assertNotNull(loader);


  }

}
