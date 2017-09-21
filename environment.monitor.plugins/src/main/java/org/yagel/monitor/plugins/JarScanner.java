package org.yagel.monitor.plugins;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.yagel.monitor.MonitorConfig;
import org.yagel.monitor.plugins.exception.PluginException;
import org.yagel.monitor.status.collector.MonitorStatusCollectorLoader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarScanner {

  private static final Logger log = Logger.getLogger(JarScanner.class);

  private static final String MONITOR_CONFIG_FILE_NAME = "EnvMonitor.xml";
  private static final Class<MonitorStatusCollectorLoader> loaderClass = MonitorStatusCollectorLoader.class;

  @Autowired
  private MonitorConfigReader reader;

  private Class classProvider;
  private String cannonicalJarPath;

  public void scanJar(String pathToJar, ClassLoader classLoader) {

    this.cannonicalJarPath = "jar:file:" + pathToJar + "!/";

    URL[] urls;
    try {
      urls = new URL[]{new URL(cannonicalJarPath)};
    } catch (IOException e) {
      throw new PluginException("Unable to locate plugin jar by path provided", e);
    }


    try (JarFile jarFile = new JarFile(pathToJar); URLClassLoader cl = URLClassLoader.newInstance(urls, classLoader)) {

      Enumeration<JarEntry> e = jarFile.entries();

      while (e.hasMoreElements()) {
        JarEntry je = e.nextElement();
        log.debug("File found: " + je.getName());

        if (this.checkIfClass(je)) {
          log.debug("Not a class, skipping");
          continue;
        }

        String className = this.getClassNameFromEntry(je);
        this.loadClass(cl, className);
        log.debug("Class loaded");
      }

    } catch (IOException e) {
      throw new PluginException("Exception occurred during loading plugin jar", e);
    }


  }

  private boolean checkIfClass(JarEntry je) {
    return je.isDirectory() || !je.getName().endsWith(".class");
  }

  private String getClassNameFromEntry(JarEntry je) {
    // -6 because of .class
    String className = je.getName().substring(0, je.getName().length() - 6);
    className = className.replace('/', '.');
    return className;
  }

  private void loadClass(URLClassLoader cl, String className) {
    try {
      log.debug("External class found: " + className);
      Class c = cl.loadClass(className);
      log.debug("Loaded class : " + className);

      if (loaderClass.isAssignableFrom(c)) {
        classProvider = c;
        log.debug("Found implementation of loader class, taking as plugin provider : " + className);
      }
    } catch (ClassNotFoundException ex) {
      throw new PluginException("For some reason was unable to load class from plugin jar", ex);
    }
  }

  public MonitorStatusCollectorLoader getStatusCollectorLoader() {
    try {
      return (MonitorStatusCollectorLoader) classProvider.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new PluginException("Unable to instantiate plugin collector class", e);
    }
  }

  public MonitorConfig getMonitorConfig() {
    return reader.readMonitorConfig(cannonicalJarPath + MONITOR_CONFIG_FILE_NAME);

  }

}
