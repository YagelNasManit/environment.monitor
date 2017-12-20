package org.yagel.monitor.runner.test.integration.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.mongo.ResourceLastStatusDAO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ResourceLastStatusDAOTest extends AbstractDAOTest {

  @Autowired
  private ResourceLastStatusDAO lastStatusDAO;

  private String environmentName = this.getClass().getSimpleName();
  private int resourcesCount = 5;

  @Test
  public void testInsertFindMultiple() throws Exception {
    List<ResourceStatus> statusList = generateN(resourcesCount, this::rndResStatus);
    lastStatusDAO.insert(environmentName, statusList);
    List<ResourceStatus> dbStatusList = lastStatusDAO.find(environmentName);


    Assert.assertTrue(dbStatusList.size() == resourcesCount);
    Assert.assertEquals(statusList, dbStatusList);


  }

  @Test(dependsOnMethods = "testInsertFindMultiple")
  public void testInsertFindMultipleWithIds() throws Exception {
    List<ResourceStatus> statusList = generateN(resourcesCount, this::rndResStatus);
    lastStatusDAO.insert(environmentName, statusList);

    Set<String> resourceIds = statusList.stream().map(resourceStatus -> resourceStatus.getResource().getId()).collect(Collectors.toSet());
    List<ResourceStatus> dbStatusList = lastStatusDAO.find(environmentName, resourceIds);


    Assert.assertEquals(dbStatusList.size(), statusList.size());
    Assert.assertEquals(statusList, dbStatusList);


  }

  @Test(dependsOnMethods = "testInsertFindMultipleWithIds")
  public void testInsertFindMultipleEnvironments() throws Exception {

    int envCount = 3;

    List<ResourceStatus> statusList = new ArrayList<>();
    Set<String> envSet = new HashSet<>(generateN(envCount,() -> UUID.randomUUID().toString()));

    for(String env:envSet) {
      List<ResourceStatus> envStatusList = generateN(resourcesCount, this::rndResStatus);
      statusList.addAll(envStatusList);
      lastStatusDAO.insert(env, envStatusList);
    }

    List<ResourceStatus> resources = lastStatusDAO.find(envSet);

    Assert.assertEquals(resources.size(), resourcesCount*envCount);
    Assert.assertTrue(resources.containsAll(statusList));


  }

  @Test(dependsOnMethods = "testInsertFindMultipleWithIds")
  public void testDelete() throws Exception {
    lastStatusDAO.delete(environmentName);

    List<ResourceStatus> emptyResources = lastStatusDAO.find(environmentName);

    Assert.assertEquals(emptyResources.size(), 0);


  }


}
