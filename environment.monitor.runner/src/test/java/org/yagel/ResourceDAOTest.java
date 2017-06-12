package org.yagel;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.yagel.monitor.Resource;
import org.yagel.monitor.mongo.MongoConnector;
import org.yagel.monitor.mongo.ResourceDAO;
import org.yagel.monitor.resource.ResourceImpl;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ResourceDAOTest {

  private String mockResoure = "Mock_resource";

  @Test
  public void testResourceDAOInsert() throws Exception {
    ResourceDAO resourceDAO = MongoConnector.getInstance().getResourceDAO();


    Resource resource = new ResourceImpl(mockResoure, "MOCK RESOURCE FOR TEST");
    resourceDAO.insert(resource);
    Resource dbResource = resourceDAO.find(mockResoure);
    Assert.assertTrue(resource.equals(dbResource));


    resource = new ResourceImpl(mockResoure, "MOCK RESOURCE FOR TEST UPDATED");
    resourceDAO.insert(resource);
    Assert.assertTrue(!resource.equals(dbResource));

    dbResource = resourceDAO.find(mockResoure);
    Assert.assertTrue(resource.equals(dbResource));


  }

  @Test(dependsOnMethods = "testResourceDAOInsert")
  public void testResourceDaoFind() {
    ResourceDAO resourceDAO = MongoConnector.getInstance().getResourceDAO();

    Resource resource = resourceDAO.find(mockResoure);

    Assert.assertNotNull(resource);
    Assert.assertEquals(resource.getId(), mockResoure);
    Assert.assertNotNull(resource.getName());

    Set<Resource> resourceSet = new HashSet<>();
    resourceSet.add(new ResourceImpl("TEST1", "MOCK RESOURCE FOR TEST UPDATED"));
    resourceSet.add(new ResourceImpl("TEST2", "MOCK RESOURCE FOR TEST UPDATED"));
    resourceSet.add(new ResourceImpl("TEST3", "MOCK RESOURCE FOR TEST UPDATED"));

    resourceDAO.insert(resourceSet);

    Set<Resource> dbResourcesSet = resourceDAO.find(resourceSet.stream().map(Resource::getId).collect(Collectors.toSet()));

    Assert.assertEquals(resourceSet, dbResourcesSet);
  }
}
