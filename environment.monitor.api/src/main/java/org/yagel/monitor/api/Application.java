package org.yagel.monitor.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.yagel.monitor.ScheduleRunner;
import org.yagel.monitor.ScheduleRunnerImpl;

@SpringBootApplication(scanBasePackages = "org.yagel.monitor")
public class Application {

  public static void main(String[] args) {
    ScheduleRunner scheduleRunner = ScheduleRunnerImpl.newInstance(ClassLoader.getSystemClassLoader());
    scheduleRunner.runTasks();
    SpringApplication.run(Application.class, args);
  }
}
