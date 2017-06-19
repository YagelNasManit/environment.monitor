package org.yagel.monitor.utils;


import org.apache.commons.lang3.time.DateUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DataUtils {

  /**
   * @param date
   * @return aggregated year and month values of given date field. Month is counted from 0
   * <p/>
   * For example:
   * If date is 2015-02-03 then return value will be 201501
   */
  public static int joinYearMonthValues(Date date) {
    Calendar calendar = DateUtils.toCalendar(date);
    return calendar.get(Calendar.YEAR) * 100 + calendar.get(Calendar.MONTH);
  }

  public static Date getYesterday(Date currentDate) {
    return DateUtils.addDays(currentDate, -1);
  }

  public static boolean isToday(LocalDateTime localDateTime) {
    return localDateTime.getDayOfYear() == LocalDateTime.now().getDayOfYear();
  }

  public static Date asDate(LocalDate localDate) {
    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }

  public static Date asDate(LocalDateTime localDateTime) {
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
  }

  public static LocalDate asLocalDate(Date date) {
    return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
  }

  public static LocalDateTime asLocalDateTime(Date date) {
    return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
  }


}
