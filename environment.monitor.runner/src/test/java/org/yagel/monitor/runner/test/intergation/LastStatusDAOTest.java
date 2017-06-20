package org.yagel.monitor.runner.test.intergation;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.mongo.MongoConnector;
import org.yagel.monitor.mongo.ResourceLastStatusDAO;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LastStatusDAOTest extends AbstractDAOTest {

  private ResourceLastStatusDAO lastStatusDAO;
  private List<ResourceStatus> statusList;
  private String environmentName = "LastStatusDAOTest";
  private int resourcesCount = 5;

  @BeforeClass
  public void loadDao() throws Exception {
    lastStatusDAO = MongoConnector.getInstance().getLastStatusDAO();
  }

  @BeforeClass
  public void buildStatusList() {
    statusList = Collections.nCopies(resourcesCount, rndResStatus());
  }

  @Test
  public void testInsertFindMultiple() throws Exception {

    lastStatusDAO.insert(environmentName, statusList);

    List<ResourceStatus> dbStatusList = lastStatusDAO.find(environmentName);


    Assert.assertTrue(dbStatusList.size() == resourcesCount);
    Assert.assertTrue(statusList.containsAll(dbStatusList));



  }

  @Test(dependsOnMethods = "testInsertFindMultiple")
  public void testInsertFindMultipleWithIds() throws Exception {


    lastStatusDAO.insert(environmentName, statusList);

    Set<String> resourceIds = statusList.stream().map(ResourceStatus::getResourceId).collect(Collectors.toSet());

    List<ResourceStatus> dbStatusList = lastStatusDAO.find(environmentName, resourceIds);


    Assert.assertTrue(dbStatusList.size() == resourcesCount);
    Assert.assertEquals(statusList, dbStatusList);


  }

  @Test(dependsOnMethods = "testInsertFindMultipleWithIds")
  public void testDelete() throws Exception {
    lastStatusDAO.delete(environmentName);

    List<ResourceStatus> emptyResources = lastStatusDAO.find(environmentName);

    Assert.assertEquals(emptyResources.size(), 0);


  }


}
