package org.yagel.monitor.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.yagel.monitor.RunnerBeansConfiguration;
import org.yagel.monitor.ScheduleRunnerImpl;

@SpringBootApplication(scanBasePackages = "org.yagel.monitor.api.rest")
@Import(RunnerBeansConfiguration.class)
public class Application {

  @Autowired
  private ScheduleRunnerImpl scheduleRunner;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public CommandLineRunner schedulingRunner() {
    return args -> scheduleRunner.runTasks(Thread.currentThread().getContextClassLoader());
  }


}
