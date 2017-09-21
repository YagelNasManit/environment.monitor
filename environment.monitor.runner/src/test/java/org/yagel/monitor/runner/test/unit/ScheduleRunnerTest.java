package org.yagel.monitor.runner.test.unit;

import org.testng.annotations.Test;
import org.yagel.monitor.exception.ScheduleRunnerException;

public class ScheduleRunnerTest extends AbstractScheduleRunnerTest {

  @Test(expectedExceptions = ScheduleRunnerException.class,
      expectedExceptionsMessageRegExp = "Plugin jar path should be provided, pls define system property: plugin.jar.location",
      priority = 1
  )
  public void testScheduleRunnerFailsWhenNoPlugin() {
    scheduleRunner.runTasks(classLoader);
    scheduleRunner.shutdown();
  }

}
