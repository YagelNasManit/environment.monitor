package org.yagel.monitor.runner.test.integration.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.mongo.ResourceStatusDetailDAO;

import java.util.List;
import java.util.UUID;

public class ResourceStatusDetailDAOTest extends AbstractDAOTest {

  @Autowired
  private ResourceStatusDetailDAO monthDetailDAO;

  private String environemntName;

  @BeforeClass
  public void setUp() throws Exception {
    String baseEnvName = this.getClass().getName();
    environemntName = baseEnvName + UUID.randomUUID();

  }

  @Test
  public void testInsertFindSingle() throws Exception {
    ResourceStatus resourceStatus = rndResStatus(rndResource());
    monthDetailDAO.insert(environemntName, resourceStatus);

    List<ResourceStatus> statuses = monthDetailDAO.getStatuses(environemntName, resourceStatus.getResource().getId(), resourceStatus.getUpdated(), resourceStatus.getUpdated());
    Assert.assertEquals(statuses.size(), 1);
    Assert.assertEquals(statuses.get(0), resourceStatus);
  }


}
