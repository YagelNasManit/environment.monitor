package org.yagel.monitor.api.test.unit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.BeforeClass;
import org.yagel.monitor.EnvironmentConfig;
import org.yagel.monitor.Resource;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.ScheduleRunnerImpl;
import org.yagel.monitor.config.EnvironmentConfigImpl;
import org.yagel.monitor.config.MonitorConfigImpl;
import org.yagel.monitor.mongo.MongoConnect;
import org.yagel.monitor.resource.ResourceImpl;
import org.yagel.monitor.resource.ResourceStatusImpl;
import org.yagel.monitor.resource.Status;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@TestExecutionListeners(MockitoTestExecutionListener.class)
public class AbstractControllerTest extends AbstractTestNGSpringContextTests {

  @MockBean
  protected ScheduleRunnerImpl scheduleRunner;

  @Autowired
  protected MockMvc mockMvc;

  protected String mockResoureId = "ControllerUnitTestResourceId";

  protected String mockResoureName = "ControllerUnitTestResourceName";
  protected Set<String> envNames;
  protected Set<String> resIds;
  protected Set<String> resNames;
  protected MonitorConfigImpl monitorConfig;
  protected Set<Resource> resourceList;


  protected List<ResourceStatus> statusList;

  protected Resource rndResource() {
    return new ResourceImpl(mockResoureId + UUID.randomUUID(), mockResoureName + UUID.randomUUID());
  }

  protected <T> List<T> generateListN(int copies, Supplier<T> supplier) {
    return Stream.generate(supplier).limit(copies).collect(Collectors.toList());
  }

  protected <T> Set<T> generateSetN(int copies, Supplier<T> supplier) {
    return new HashSet<>(generateListN(copies, supplier));
  }


  @BeforeClass
  public void configureResources() {
    resourceList = generateSetN(10, this::rndResource);
    resIds = resourceList.stream().map(Resource::getId).collect(Collectors.toSet());
    resNames = resourceList.stream().map(Resource::getName).collect(Collectors.toSet());
  }

  @BeforeClass(dependsOnMethods = "configureResources")
  public void configureEnvironments() {
    envNames = new HashSet<>();
    envNames.add("UnitTest1");
    envNames.add("UnitTest2");
    envNames.add("UnitTest3");


    Set<EnvironmentConfig> envConfs = new HashSet<>();

    for (String envName : envNames) {
      EnvironmentConfigImpl environmentConfig = new EnvironmentConfigImpl();
      environmentConfig.setEvnName(envName);
      environmentConfig.setCheckedResources(resIds);
      envConfs.add(environmentConfig);
    }

    monitorConfig = new MonitorConfigImpl();
    monitorConfig.setEnvironments(envConfs);
  }

  @BeforeClass(dependsOnMethods = "configureEnvironments")
  public void configureResourceStatuses() {

    this.statusList = resourceList.stream().map(resource ->
        new ResourceStatusImpl(resource, Status.Online, new Date())).collect(Collectors.toList()
    );
  }

}
