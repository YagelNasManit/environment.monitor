package org.yagel.monitor.runner.test.intergation.dao;

import org.apache.commons.lang3.time.DateUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.yagel.monitor.Resource;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.mongo.MongoConnector;
import org.yagel.monitor.mongo.ResourceMonthDetailDAO;
import org.yagel.monitor.resource.Status;

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

    String baseEnvName = this.getClass().getName();
    environemntNames = new String[]{baseEnvName + "1", baseEnvName + "2"};

    endDate = new Date();
    startDate = DateUtils.addDays(endDate, -1);

    populateDB();
  }

  private void populateDB() {
    resource = rndResource();

    for (String environmentName : environemntNames) {

      List<ResourceStatus> statusesOnline = generateN(100, () -> rndResStatus(resource, Status.Online, rndDate(startDate, endDate)));
      List<ResourceStatus> statusesUnknown = generateN(100, () -> rndResStatus(resource, Status.Unavailable, rndDate(startDate, endDate)));
      List<ResourceStatus> statusesUnavailable = generateN(100, () -> rndResStatus(resource, Status.Unknown, rndDate(startDate, endDate)));

      monthDetailDAO.insert(environmentName, statusesOnline);
      monthDetailDAO.insert(environmentName, statusesUnavailable);
      monthDetailDAO.insert(environmentName, statusesUnknown);
    }
  }

  @Test
  public void testInsertFindSingle() throws Exception {
    ResourceStatus resourceStatus = rndResStatus(rndResource());
    monthDetailDAO.insert(environemntNames[0], resourceStatus);

    List<ResourceStatus> statuses = monthDetailDAO.getStatuses(environemntNames[0], resourceStatus.getResource().getId(), resourceStatus.getUpdated(), resourceStatus.getUpdated());
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

    for (String env : environemntNames) {

      Map<Resource, Map<Status, Integer>> states = monthDetailDAO.getAggregatedStatuses(env, startDate, endDate);

      Assert.assertNotNull(states);
      Assert.assertTrue(states.size() > 0);
      Assert.assertTrue(states.size() > 0);


      Map<Status, Integer> resourceStates = states.get(resource);

      Assert.assertEquals(resourceStates.keySet().size(), 3);
      resourceStates.forEach((Status status, Integer value) -> Assert.assertEquals((int) value, 100));
    }



  }
}
