package org.yagel.monitor;


import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.yagel.monitor.mongo.AggregatedStatusDAO;
import org.yagel.monitor.mongo.MongoConnect;
import org.yagel.monitor.mongo.ResourceDAO;
import org.yagel.monitor.mongo.ResourceLastStatusDAO;
import org.yagel.monitor.mongo.ResourceStatusDetailDAO;
import org.yagel.monitor.plugins.PluginsBeansConfiguration;

@Configuration
@Import(PluginsBeansConfiguration.class)
public class RunnerBeansConfiguration {

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
  public MongoConnect createDatabase() {
    return new MongoConnect();
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
  public ResourceDAO createResourceDAO(MongoConnect connect) {
    return new ResourceDAO(connect);
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
  public ResourceLastStatusDAO createResourceLastStatusDAO(MongoConnect connect) {
    return new ResourceLastStatusDAO(connect);
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
  public ResourceStatusDetailDAO createResourceStatusDetailDAO(MongoConnect connect) {
    return new ResourceStatusDetailDAO(connect);
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
  public AggregatedStatusDAO createAggregatedStatusDAO(MongoConnect connect) {
    return new AggregatedStatusDAO(connect);
  }


  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
  public ScheduleRunnerImpl createScheduleRunner(){
    return new ScheduleRunnerImpl();
  }

}
