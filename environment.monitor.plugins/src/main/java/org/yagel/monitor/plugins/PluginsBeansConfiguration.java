package org.yagel.monitor.plugins;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PluginsBeansConfiguration {

  @Bean
  public JarScanner createJarScanner(){
    return new JarScanner();
  }

  @Bean
  public MonitorConfigReader createConfigReader(){
    return new MonitorConfigReader();
  }
}
