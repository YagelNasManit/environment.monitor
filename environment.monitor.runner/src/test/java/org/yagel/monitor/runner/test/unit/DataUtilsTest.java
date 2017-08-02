package org.yagel.monitor.runner.test.unit;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.yagel.monitor.utils.DataUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DataUtilsTest {

  @Test
  public void testJoinYearAndMonthFirstMonthOfYear() throws Exception {

    LocalDate localDateTime = LocalDate.of(2017, 1, 1);
    int joinedYearAndMonth = DataUtils.joinYearMonthValues(DataUtils.asDate(localDateTime));
    Assert.assertEquals(joinedYearAndMonth, 201700);

  }

  @Test
  public void testJoinYearAndMonthLastMonthOfYear() throws Exception {
    LocalDate localDateTime = LocalDate.of(2017, 12, 1);
    int joinedYearAndMonth = DataUtils.joinYearMonthValues(DataUtils.asDate(localDateTime));
    Assert.assertEquals(joinedYearAndMonth, 201711);
  }


  @Test
  public void testYesterdayCalculation() throws Exception {

    Date currentDate = new Date();
    Date date = DataUtils.getYesterday(currentDate);


    Assert.assertTrue(date.before(currentDate));

    LocalDateTime currentDateTime = DataUtils.asLocalDateTime(currentDate);
    LocalDateTime dateTime = DataUtils.asLocalDateTime(date);

    Assert.assertEquals(currentDateTime.getYear(), dateTime.getYear());
    Assert.assertEquals(currentDateTime.getMonth(), dateTime.getMonth());
    Assert.assertEquals(currentDateTime.getHour(), dateTime.getHour());
    Assert.assertEquals(currentDateTime.getMinute(), dateTime.getMinute());

    Assert.assertEquals(currentDateTime.getDayOfMonth() - 1, dateTime.getDayOfMonth());

  }

  @Test
  public void testIsTodayCalculation() {
    LocalDateTime localDateTime = LocalDateTime.now();

    Assert.assertTrue(DataUtils.isToday(localDateTime));
    Assert.assertTrue(DataUtils.isToday(LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MIN)));
    Assert.assertTrue(DataUtils.isToday(LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MAX)));
    Assert.assertFalse(DataUtils.isToday(localDateTime.minusDays(1)));

  }


  @Test
  public void testMonthEnumeration() {
    LocalDateTime end = LocalDateTime.of(2017, 12, 1, 3, 1);
    LocalDateTime start = LocalDateTime.of(2017, 1, 1, 3, 1);


    List<Integer> monthNumbers = DataUtils.getMonthNumbersInDateFrame(DataUtils.asDate(start), DataUtils.asDate(end));

    Assert.assertEquals(monthNumbers.size(), 12);
    Assert.assertEquals(monthNumbers, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
  }
}
