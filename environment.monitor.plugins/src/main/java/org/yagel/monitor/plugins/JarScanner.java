package org.yagel.monitor.plugins;

import org.apache.log4j.Logger;
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

  private final static Logger log = Logger.getLogger(JarScanner.class);
  private final static String MONITOR_CONFIG_FILE_NAME = "EnvMonitor.xml";
  private final static Class<MonitorStatusCollectorLoader> loaderClass = MonitorStatusCollectorLoader.class;
  private final ClassLoader classLoader;
  private Class classProvider;
  private String pathToJar;
  private String cannonicalJarPath;

  public JarScanner(ClassLoader classLoader, String pathToJar) {
    this.classLoader = classLoader;
    this.pathToJar = pathToJar;
    this.cannonicalJarPath = "jar:file:" + pathToJar + "!/";
  }

  public void scanJar() {

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
        if (je.isDirectory() || !je.getName().endsWith(".class")) {
          log.debug("not a class, skip");
          continue;
        }

        // -6 because of .class
        String className = je.getName().substring(0, je.getName().length() - 6);
        className = className.replace('/', '.');


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

    } catch (IOException e) {
      throw new PluginException("Exception occurred during loading plugin jar", e);
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


    return new MonitorConfigReader().readMonitorConfig(cannonicalJarPath + MONITOR_CONFIG_FILE_NAME);

  }

}
