package org.yagel.monitor.runner.test.intergation;

import org.apache.commons.lang3.time.DateUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.yagel.monitor.Resource;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.mongo.MongoConnector;
import org.yagel.monitor.mongo.ResourceMonthDetailDAO;
import org.yagel.monitor.resource.ResourceStatusImpl;
import org.yagel.monitor.resource.Status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ResourceMonthDetailDAOTest extends AbstractDAOTest {

  private ResourceMonthDetailDAO monthDetailDAO;

  private String[] environemntNames;
  private Date startDate;
  private Date endDate;
  private Resource resource;


  @BeforeClass
  public void setUp() throws Exception {
    monthDetailDAO = MongoConnector.getInstance().getMonthDetailDAO();


    environemntNames = new String[]{"ResourceMonthDetailDAOTest1", "ResourceMonthDetailDAOTest2", "ResourceMonthDetailDAOTest3"};
    endDate = new Date();
    startDate = DateUtils.addDays(endDate, -1);

    populateDB();
  }

  private void populateDB() {
    resource = rndResource();

    for (String environmentName : environemntNames) {

      List<ResourceStatus> statusesOnline = new ArrayList<>();
      List<ResourceStatus> statusesUnknown = new ArrayList<>();
      List<ResourceStatus> statusesUnavailable = new ArrayList<>();

      for (int i = 0; i < 100; i++) {
        statusesOnline.add(new ResourceStatusImpl(resource.getId(), Status.Online, rndDate(startDate, endDate)));
        statusesUnavailable.add(new ResourceStatusImpl(resource.getId(), Status.Unavailable, rndDate(startDate, endDate)));
        statusesUnknown.add(new ResourceStatusImpl(resource.getId(), Status.Unknown, rndDate(startDate, endDate)));
      }

      monthDetailDAO.insert(environmentName, statusesOnline);
      monthDetailDAO.insert(environmentName, statusesUnavailable);
      monthDetailDAO.insert(environmentName, statusesUnknown);
    }
  }

  @Test(dependsOnMethods = {"testResourceMonthDetailDAOCount", "testResourceMonthDetailDAOGetAggregatedStatuses"})
  public void testInsertFindSingle() throws Exception {
    ResourceStatus resourceStatus = rndResStatus();
    monthDetailDAO.insert(environemntNames[0], resourceStatus);

    List<ResourceStatus> statuses = monthDetailDAO.getStatuses(environemntNames[0], resourceStatus.getResourceId(), resourceStatus.getUpdated(), resourceStatus.getUpdated());
    Assert.assertEquals(statuses.size(), 1);
    Assert.assertEquals(statuses.get(0), resourceStatus);
  }


  @Test
  public void testResourceMonthDetailDAOCount() throws Exception {
    String environemntName = environemntNames[0];

    long statusOnlineCount = monthDetailDAO.getStatusCount(environemntName, resource.getId(), Status.Online, startDate, endDate);
    Assert.assertTrue(statusOnlineCount > 0);
    Assert.assertEquals(statusOnlineCount, 100);

    long statusUnavailableCount = monthDetailDAO.getStatusCount(environemntName, resource.getId(), Status.Unavailable, startDate, endDate);
    Assert.assertTrue(statusUnavailableCount > 0);
    Assert.assertEquals(statusUnavailableCount, 100);

    long statusUnknownCount = monthDetailDAO.getStatusCount(environemntName, resource.getId(), Status.Unknown, startDate, endDate);
    Assert.assertTrue(statusUnknownCount > 0);
    Assert.assertEquals(statusUnknownCount, 100);

  }


  @Test
  public void testResourceMonthDetailDAOGetAggregatedStatuses() throws Exception {

    Map<String, Map<Status, Integer>> states = monthDetailDAO.getAggregatedStatuses(environemntNames[0], startDate, endDate);
    Map<String, Map<Status, Integer>> states1 = monthDetailDAO.getAggregatedStatuses(environemntNames[1], startDate, endDate);
    Map<String, Map<Status, Integer>> states2 = monthDetailDAO.getAggregatedStatuses(environemntNames[2], startDate, endDate);


    Assert.assertNotNull(states);
    Assert.assertEquals(states, states1);
    Assert.assertEquals(states1, states2);
    Assert.assertTrue(states.size() > 0);

    Assert.assertEquals(states.keySet().size(), 1);
    Assert.assertEquals(states1.keySet().size(), 1);
    Assert.assertEquals(states2.keySet().size(), 1);

    Assert.assertEquals(states.keySet().size(), 1);
    Assert.assertEquals(states1.keySet().size(), 1);
    Assert.assertEquals(states2.keySet().size(), 1);



  }
}
