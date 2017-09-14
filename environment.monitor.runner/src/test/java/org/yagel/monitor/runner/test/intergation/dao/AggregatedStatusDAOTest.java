package org.yagel.monitor.runner.test.intergation.dao;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.yagel.monitor.Resource;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.mongo.AggregatedStatusDAO;
import org.yagel.monitor.mongo.ResourceStatusDetailDAO;
import org.yagel.monitor.resource.AggregatedResourceStatus;
import org.yagel.monitor.resource.Status;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AggregatedStatusDAOTest extends AbstractDAOTest {

  @Autowired
  private AggregatedStatusDAO aggregatedStatusDAO;

  @Autowired
  private ResourceStatusDetailDAO monthDetailDAO;

  private String[] environemntNames;
  private Date startDate;
  private Date endDate;
  private Resource resource;


  @BeforeClass
  public void setUp() throws Exception {
    String baseEnvName = this.getClass().getName();
    environemntNames = new String[]{baseEnvName + UUID.randomUUID(), baseEnvName + UUID.randomUUID()};

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


  @Deprecated
  @Test(enabled = false)
  public void testResourceMonthDetailDAOCount() throws Exception {
    String environemntName = environemntNames[0];

    long statusOnlineCount = aggregatedStatusDAO.getStatusCount(environemntName, resource.getId(), Status.Online, startDate, endDate);
    Assert.assertTrue(statusOnlineCount > 0);
    Assert.assertEquals(statusOnlineCount, 100);

    long statusUnavailableCount = aggregatedStatusDAO.getStatusCount(environemntName, resource.getId(), Status.Unavailable, startDate, endDate);
    Assert.assertTrue(statusUnavailableCount > 0);
    Assert.assertEquals(statusUnavailableCount, 100);

    long statusUnknownCount = aggregatedStatusDAO.getStatusCount(environemntName, resource.getId(), Status.Unknown, startDate, endDate);
    Assert.assertTrue(statusUnknownCount > 0);
    Assert.assertEquals(statusUnknownCount, 100);

  }


  @Test
  public void testResourceMonthDetailDAOGetAggregatedStatuses() throws Exception {

    for (String env : environemntNames) {

      List<AggregatedResourceStatus> states = aggregatedStatusDAO.getAggregatedStatuses(env, startDate, endDate);

      Assert.assertNotNull(states);
      Assert.assertTrue(states.size() > 0);
      Assert.assertTrue(states.size() > 0);


      AggregatedResourceStatus status = states.get(0);

      Assert.assertEquals(status.getResourceStatuses().size(), 3);

      Assert.assertEquals(status.getCount(), 300);
      status.getResourceStatuses().forEach(value -> Assert.assertEquals(value.getCount(), 100));
    }
  }

  @Test
  public void testResourceMonthDetailDAOGetAggregatedStatusesMonthIterate() throws Exception {
    String environmentName = this.getClass().getName() + UUID.randomUUID();

    final Date endDate = new Date();
    final Date startDate = DateUtils.addDays(endDate, -35);

    final Resource resource = rndResource();

    List<ResourceStatus> statusesOnline = generateN(500, () -> rndResStatus(resource, Status.Online, rndDate(startDate, endDate)));
    List<ResourceStatus> statusesUnknown = generateN(500, () -> rndResStatus(resource, Status.Unavailable, rndDate(startDate, endDate)));
    List<ResourceStatus> statusesUnavailable = generateN(500, () -> rndResStatus(resource, Status.Unknown, rndDate(startDate, endDate)));

    monthDetailDAO.insert(environmentName, statusesOnline);
    monthDetailDAO.insert(environmentName, statusesUnavailable);
    monthDetailDAO.insert(environmentName, statusesUnknown);

    List<AggregatedResourceStatus> statusList = aggregatedStatusDAO.getAggregatedStatuses(environmentName, startDate, endDate);

    Assert.assertEquals(statusList.stream().mapToLong(AggregatedResourceStatus::getCount).sum(), 1500);
    Assert.assertEquals(statusList.size(), 1);
    Assert.assertEquals(statusList.get(0).getCount(), 1500);
    Assert.assertEquals(statusList.get(0).getResourceStatuses().size(), 3);
    Assert.assertEquals(statusList.get(0).getResourceStatuses().get(0).getCount(), 500);
    Assert.assertEquals(statusList.get(0).getResourceStatuses().get(1).getCount(), 500);
    Assert.assertEquals(statusList.get(0).getResourceStatuses().get(2).getCount(), 500);


  }

}
