package org.yagel.monitor.runner.test.integration.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.yagel.monitor.Resource;
import org.yagel.monitor.mongo.ResourceDAO;
import org.yagel.monitor.resource.ResourceImpl;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ResourceDAOTest extends AbstractDAOTest {

  @Autowired
  private ResourceDAO resourceDAO;

  @Test
  public void testInsertFindSingle() throws Exception {
    Resource resource = rndResource();
    resourceDAO.insert(resource);
    Resource dbResource = resourceDAO.find(resource.getId());

    Assert.assertEquals(resource, dbResource);
    Assert.assertNotNull(dbResource);
    Assert.assertEquals(dbResource.getId(), resource.getId());
    Assert.assertNotNull(dbResource.getName());

  }

  @Test
  public void testResourceUpsert() {
    // check inserted
    Resource resource = rndResource();
    resourceDAO.insert(resource);
    Resource resourceUpdated = new ResourceImpl(resource.getId(), "MOCK RESOURCE FOR TEST UPDATED");
    resourceDAO.insert(resourceUpdated);
    Resource dbResourceUpdated = resourceDAO.find(resource.getId());

    Assert.assertNotEquals(resource, resourceUpdated);
    Assert.assertEquals(resourceUpdated, dbResourceUpdated);
  }


  @Test(dependsOnMethods = "testInsertFindSingle")
  public void testInsertFindMultiple() {
    Set<Resource> mockResources = new HashSet<>(generateN(5, this::rndResource));

    Set<String> mockResourcesIds = mockResources.stream().map(Resource::getId).collect(Collectors.toSet());
    resourceDAO.insert(mockResources);

    Set<Resource> dbResources = resourceDAO.find(mockResourcesIds);

    Assert.assertEquals(mockResources, dbResources);
  }
}
