package org.yagel.monitor.utils;


import org.apache.commons.lang3.time.DateUtils;
import org.yagel.monitor.ResourceStatus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DataUtils {

  /**
   * Calculates received status running duration in minutes
   *
   * @param resources      resources with statuses
   * @param resourceStatus resource's status
   * @return status duration
   */
  public static double calculateStatusDuration(List<? extends ResourceStatus> resources, ResourceStatus resourceStatus) {
    double duration = 0;
    double currentMinute;
    double nextMinute;
    double minutesBetween;

    for (int i = 0; i < resources.size() - 1; i++) {

      if (resources.get(i).getStatus().equals(resourceStatus)) {

        currentMinute = resources.get(i).getUpdated().getTime();
        nextMinute = resources.get(i + 1).getUpdated().getTime();
        minutesBetween = (nextMinute - currentMinute) / 60000;
        duration += minutesBetween;
      }
    }
    return duration;
  }

  /**
   * Get status duration in percent
   *
   * @param totalDuration  total statuses duration
   * @param statusDuration particular status duration
   * @return status duration in percent
   */
  public static long calculateStatusPercent(double totalDuration, double statusDuration) {
    return Math.round((statusDuration * 100) / totalDuration);
  }

  /**
   * Get end date of months
   *
   * @param date
   * @return end date of months
   */
  public static Calendar calculateEndDateOfMonth(Calendar date) {
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT + 3"));
    calendar.set(Calendar.DATE, date.getActualMaximum(Calendar.DATE));
    calendar.set(Calendar.MONTH, date.get(Calendar.MONTH));
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    return calendar;
  }

  /**
   * Get start date of months
   *
   * @param months
   * @return start date of months
   */
  public static Calendar calculateStartDateOfMonths(int months) {
    Calendar newStartDate = Calendar.getInstance(TimeZone.getTimeZone("GMT + 3"));
    newStartDate.set(Calendar.DATE, newStartDate.getActualMinimum(Calendar.DATE));
    newStartDate.set(Calendar.MONTH, months);
    newStartDate.set(Calendar.HOUR_OF_DAY, 0);
    newStartDate.set(Calendar.MINUTE, 0);
    newStartDate.set(Calendar.SECOND, 0);
    newStartDate.set(Calendar.MILLISECOND, 0);
    return newStartDate;
  }

  /**
   * Get "EEE, MMM d yyyy, HH:mm:ss" format
   *
   * @param date date for visualization modification
   * @return date in new format
   */
  public static String format(Date date) {
    return format(date, "EEE, MMM d yyyy, HH:mm:ss");
  }

  /**
   * Get "EEE, MMM d yyyy, HH:mm:ss" format
   *
   * @param date    date for visualization modification
   * @param pattern the pattern describing the date and time format
   * @return date in new format
   */
  public static String format(Date date, String pattern) {
    return new SimpleDateFormat(pattern).format(date);
  }

  /**
   * @param date
   * @param endDay if true get midnight on end of day
   * @return rounded up  date to midnight on begin or end of date
   */
  public static Date calculateMidnight(Date date, boolean endDay) {
    if (endDay) {
      return DateUtils.addMilliseconds(DateUtils.ceiling(date, Calendar.DATE), -1);
    } else {
      return DateUtils.truncate(date, Calendar.DATE);
    }
  }

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


   /* public static Date setDateCurrentTime(Date date) {
        LocalTime time = LocalTime.now();

        date = DateUtils.setHours(date, time.getHourOfDay());
        date = DateUtils.setMinutes(date, time.getMinuteOfHour());
        date = DateUtils.setSeconds(date, time.getSecondOfMinute());

        return date;
    }*/

  public static Date getYesterday(Date currentDate) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(currentDate);
    calendar.add(Calendar.DAY_OF_MONTH, -1);
    return calendar.getTime();

  }
}
