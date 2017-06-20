package org.yagel.monitor.runner.test.intergation;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.yagel.monitor.Resource;
import org.yagel.monitor.mongo.MongoConnector;
import org.yagel.monitor.mongo.ResourceDAO;
import org.yagel.monitor.resource.ResourceImpl;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ResourceDAOTest extends AbstractDAOTest {


  private ResourceDAO resourceDAO;


  @BeforeClass
  public void loadDao() {
    resourceDAO = MongoConnector.getInstance().getResourceDAO();
  }

  @Test
  public void testInsertFindSingle() throws Exception {
    // check inserted
    Resource resource = rndResource();
    resourceDAO.insert(resource);

    Resource dbResource = resourceDAO.find(resource.getId());
    Assert.assertEquals(resource, dbResource);

    // check description changed
    resource = new ResourceImpl(mockResoureId, "MOCK RESOURCE FOR TEST UPDATED");
    resourceDAO.insert(resource);
    Assert.assertNotEquals(resource, dbResource);

    // check description changed properly
    dbResource = resourceDAO.find(mockResoureId);
    Assert.assertEquals(resource, dbResource);


  }

  @Test(dependsOnMethods = "testInsertFindSingle")
  public void testInsertFindMultiple() {

    // build resources to be inserted
    Set<Resource> mockResourcesSet = new HashSet<>();
    for (int i = 0; i < 5; i++)
      mockResourcesSet.add(
          rndResource()
      );

    // check if inserted
    resourceDAO.insert(mockResourcesSet);
    Set<Resource> dbResourcesSet = resourceDAO.find(mockResourcesSet.stream().map(Resource::getId).collect(Collectors.toSet()));
    Assert.assertEquals(mockResourcesSet, dbResourcesSet);
  }

  @Test(dependsOnMethods = "testInsertFindSingle")
  public void testFindSingle() throws Exception {
    Resource resource = resourceDAO.find(mockResoureId);

    Assert.assertNotNull(resource);
    Assert.assertEquals(resource.getId(), mockResoureId);
    Assert.assertNotNull(resource.getName());
  }
}
