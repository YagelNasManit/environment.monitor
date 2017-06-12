package org.yagel;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.yagel.monitor.ResourceStatus;
import org.yagel.monitor.mongo.MongoConnector;
import org.yagel.monitor.mongo.ResourceMonthDetailDAO;
import org.yagel.monitor.resource.ResourceStatusImpl;
import org.yagel.monitor.resource.Status;
import org.yagel.monitor.utils.DataUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class ResourceMonthDetailDAOTest {

  @Test
  public void testResourceMonthDetailDAO() throws Exception {
    ResourceMonthDetailDAO monthDetailDAO = MongoConnector.getInstance().getMonthDetailDAO();

    ResourceStatus resourceStatus = new ResourceStatusImpl("mock", Status.Unavailable, new Date());
    monthDetailDAO.insert("test", resourceStatus);


  }


  @Test
  public void testResourceMonthDetailDAOCount() throws Exception {
    ResourceMonthDetailDAO monthDetailDAO = MongoConnector.getInstance().getMonthDetailDAO();

    long statusCount = monthDetailDAO.getStatusCount("Local", "Google", Status.Unavailable, DataUtils.getYesterday(new Date()), new Date());
    Assert.assertTrue(statusCount > 0);

  }


  @Test
  public void testResourceMonthDetailDAOGetAggregatedStatuses() throws Exception {
    ResourceMonthDetailDAO monthDetailDAO = MongoConnector.getInstance().getMonthDetailDAO();


    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, -2);
    Date from = calendar.getTime();

    Map<String, Map<Status, Integer>> states = monthDetailDAO.getAggregatedStatuses("Local", from, new Date());

    Assert.assertNotNull(states);
    Assert.assertTrue(states.size() > 0);

  }
}
