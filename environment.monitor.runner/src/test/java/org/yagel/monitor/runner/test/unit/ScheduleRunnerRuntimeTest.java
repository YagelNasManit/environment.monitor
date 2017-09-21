package org.yagel.monitor.runner.test.unit;

import static org.mockito.BDDMockito.given;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.MonitorStatusCollector;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.UpdateStatusListener;
import org.yagel.monitor.resource.ResourceStatusImpl;
import org.yagel.monitor.status.collector.MonitorStatusCollectorLoader;

import java.util.HashSet;
import java.util.Set;

public class ScheduleRunnerRuntimeTest extends AbstractScheduleRunnerTest {

  private static final Logger logger = Logger.getLogger(ScheduleRunnerRuntimeTest.class);

  private SoftAssert softAssert = new SoftAssert();

  @BeforeClass
  public void configure() {
    given(jarScanner.getStatusCollectorLoader()).willReturn(new UnitTestCollectorLoader());

  }

  @Test(priority = 2)
  public void testScheduleRunner() {
    System.setProperty("plugin.jar.location", "");

    scheduleRunner.runTasks(classLoader);
    scheduleRunner.addListener(new UnitTestAssertionListener());

    this.forceMainSleepFor(60_000);
    scheduleRunner.shutdown();
    softAssert.assertAll();
  }

  private void forceMainSleepFor(long mills) {
    try {
      Thread.sleep(mills);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private class UnitTestCollectorLoader implements MonitorStatusCollectorLoader {

    @Override
    public MonitorStatusCollector loadCollector(EnvironmentConfig config) {

      return () -> {
        logger.debug("loading unit test  resource statuses");
        Set<ResourceStatus> statusSet = new HashSet<>();
        ResourceStatus status = new ResourceStatusImpl(ScheduleRunnerRuntimeTest.this.resource, ScheduleRunnerRuntimeTest.this.status);
        statusSet.add(status);
        return statusSet;
      };

    }
  }

  private class UnitTestAssertionListener implements UpdateStatusListener {
    @Override
    public String getEnvName() {
      return ScheduleRunnerRuntimeTest.this.envName;
    }

    @Override
    public void update(Set<ResourceStatus> lastChangedStatus) {
      logger.debug("checking unit test  resource statuses results");
      for (ResourceStatus resourceStatus : lastChangedStatus) {
        softAssert.assertNotNull(resourceStatus.getResource());
        softAssert.assertNotNull(resourceStatus.getUpdated());
        softAssert.assertEquals(resourceStatus.getResource().getName(), resource.getName());
        softAssert.assertEquals(resourceStatus.getResource().getId(), resource.getId());
        softAssert.assertEquals(resourceStatus.getStatus(), status);

      }
      logger.debug("checking finished");
    }

    @Override
    public boolean isActive() {
      return true;
    }
  }


}
