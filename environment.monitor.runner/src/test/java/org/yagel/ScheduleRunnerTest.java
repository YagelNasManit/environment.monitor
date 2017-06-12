package org.yagel;

import org.testng.annotations.Test;
import org.yagel.monitor.ScheduleRunner;
import org.yagel.monitor.ScheduleRunnerImpl;

public class ScheduleRunnerTest {

  @Test(enabled = false)
  public void testScheduleRunner() throws Exception {
    ScheduleRunner scheduleRunner = ScheduleRunnerImpl.newInstance(ClassLoader.getSystemClassLoader());
    scheduleRunner.runTasks();

    Thread.sleep(500_000);

  }
}
