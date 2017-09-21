package org.yagel.monitor.runner.test.unit;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.RunnerBeansConfiguration;
import org.yagel.monitor.ScheduleRunnerImpl;
import org.yagel.monitor.config.EnvironmentConfigImpl;
import org.yagel.monitor.config.MonitorConfigImpl;
import org.yagel.monitor.mongo.MongoConnect;
import org.yagel.monitor.mongo.ResourceDAO;
import org.yagel.monitor.mongo.ResourceLastStatusDAO;
import org.yagel.monitor.mongo.ResourceStatusDetailDAO;
import org.yagel.monitor.plugins.JarScanner;
import org.yagel.monitor.resource.ResourceImpl;
import org.yagel.monitor.resource.Status;

import java.util.HashSet;
import java.util.Set;

@ContextConfiguration(classes = RunnerBeansConfiguration.class)
@TestExecutionListeners(MockitoTestExecutionListener.class)
public abstract class AbstractScheduleRunnerTest extends AbstractTestNGSpringContextTests {


  @MockBean
  protected ClassLoader classLoader;
  @MockBean
  protected JarScanner jarScanner;
  @Autowired
  protected ScheduleRunnerImpl scheduleRunner;
  protected ResourceImpl resource;
  protected Status status;
  protected String envName;
  @MockBean
  private MongoConnect mongoConnect;
  @MockBean
  private ResourceDAO resourceDAO;
  @MockBean
  private ResourceLastStatusDAO lastStatusDAO;
  @MockBean
  private ResourceStatusDetailDAO statusDetailDAO;
  private MonitorConfigImpl monitorConfig;

  @BeforeClass
  public void configureEnvironments() {
    this.envName = "Schedule Runner Test";
    this.resource = new ResourceImpl("UnitTestResId", "UnitTestResName");
    this.status = Status.Online;

    Set<String> resSet = new HashSet<>();
    resSet.add(resource.getId());

    EnvironmentConfigImpl environmentConfig = new EnvironmentConfigImpl();
    environmentConfig.setEvnName(envName);
    environmentConfig.setCheckedResources(resSet);
    environmentConfig.setTaskDelay(1);


    Set<EnvironmentConfig> configSet = new HashSet<>();
    configSet.add(environmentConfig);

    this.monitorConfig = new MonitorConfigImpl();
    this.monitorConfig.setEnvironments(configSet);

  }

  @BeforeClass
  public void configureMocks() {
    given(jarScanner.getMonitorConfig()).willReturn(monitorConfig);
    willDoNothing().given(jarScanner).scanJar(Mockito.anyString(), Mockito.any());
    willDoNothing().given(lastStatusDAO).insert(Mockito.anyString(), Mockito.anySet());
    willDoNothing().given(statusDetailDAO).insert(Mockito.anyString(), Mockito.anySet());
    willDoNothing().given(resourceDAO).insert(Mockito.anySet());

  }


}
