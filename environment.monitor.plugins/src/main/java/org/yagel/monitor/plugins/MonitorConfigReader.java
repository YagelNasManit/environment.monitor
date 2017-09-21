package org.yagel.monitor.plugins;

import org.apache.log4j.Logger;
import org.yagel.monitor.MonitorConfig;
import org.yagel.monitor.config.EnvironmentConfigImpl;
import org.yagel.monitor.config.MonitorConfigImpl;
import org.yagel.monitor.plugins.exception.MonitorConfigDeserializationException;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class MonitorConfigReader {

  private final static Logger log = Logger.getLogger(MonitorConfigReader.class);

  public MonitorConfig readMonitorConfig(String configUrl) {

    try {
      log.info("Loading configuration");

      JAXBContext context;
      context = JAXBContext.newInstance(MonitorConfigImpl.class, EnvironmentConfigImpl.class);
      Unmarshaller um = context.createUnmarshaller();
      MonitorConfig config = (MonitorConfig) um.unmarshal(new URL(configUrl));

      log.info("Loading configuration - done.");

      return config;
    } catch (JAXBException | MalformedURLException e) {
      log.error("Error during configuration deserialization s" + e.getMessage());
      throw new MonitorConfigDeserializationException(e);

    }


  }

}
