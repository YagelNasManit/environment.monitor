package org.yagel;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.yagel.monitor.Resource;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.mongo.MongoConnector;
import org.yagel.monitor.mongo.ResourceLastStatusDAO;
import org.yagel.monitor.resource.ResourceImpl;
import org.yagel.monitor.resource.ResourceStatusImpl;
import org.yagel.monitor.resource.Status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LastStatusDAOTest {

  @Test
  public void testLastStatusDAO() throws Exception {
    MongoConnector mongoConnector = MongoConnector.getInstance();

    ResourceLastStatusDAO lastStatusDAO = mongoConnector.getLastStatusDAO();


    Resource resource = new ResourceImpl("mpckRes", "External resource: MOCK");
    ResourceStatus resourceStatus = new ResourceStatusImpl(resource.getId(), Status.Unavailable);
    resourceStatus.setUpdated(new Date());

    List<ResourceStatus> statusList = new ArrayList<>();
    statusList.add(resourceStatus);


    lastStatusDAO.insert("test", statusList);

    List<ResourceStatus> resources = lastStatusDAO.find("test");


    Assert.assertTrue(resources.size() == 1);
    Assert.assertTrue(resources.contains(resourceStatus));
    Assert.assertTrue(resources.get(0).equals(resourceStatus));


  }
}
