package org.yagel.monitor.plugins;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.yagel.monitor.status.collector.MonitorStatusCollectorLoader;

import java.io.IOException;
import javax.xml.bind.JAXBException;

public class TestAddJarToClassPath {

  @Test(enabled = false)
  public void testLoaderIsFound() throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, JAXBException {


    String pathToLocalJar = "/Users/oleh_kovalyshyn/Self_Development/Github/EnvMonitor/source/plugin-extension/target/plugin-extension-1.0-SNAPSHOT.jar";

    JarScanner collectorFinder = new JarScanner(ClassLoader.getSystemClassLoader(), pathToLocalJar);

    collectorFinder.scanJar();
    MonitorStatusCollectorLoader loader = collectorFinder.getStatusCollectorLoader();

    Assert.assertNotNull(loader);


  }

}
