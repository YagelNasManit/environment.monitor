package org.yagel.monitor.runner.test.intergation.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.mongo.ResourceLastStatusDAO;

import java.util.List;
import java.util.Set;
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
  public void testDelete() throws Exception {
    lastStatusDAO.delete(environmentName);

    List<ResourceStatus> emptyResources = lastStatusDAO.find(environmentName);

    Assert.assertEquals(emptyResources.size(), 0);


  }


}
