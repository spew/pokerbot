package org.poker.irc.scc;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;

public enum SceneShow {
  THE_AMERICANS("The Americans", DateTimeConstants.THURSDAY, 3),
  MAD_MEN("Mad Men", DateTimeConstants.MONDAY, 3),
  SILICON_VALLEY("Silicon Valley", DateTimeConstants.MONDAY, 3),
  GAME_OF_THRONES("Game of Thrones", DateTimeConstants.MONDAY, 2),
  VEEP("Veep", DateTimeConstants.MONDAY, 3),
  REAL_TIME("Real Time", DateTimeConstants.SATURDAY, 6);

  private String name;
  private int likelyReleaseDayOfWeek;
  private int likelyReleaseHourOfDay;

  SceneShow(String name, int likelyReleaseDayOfWeek, int likelyReleaseHourOfDay) {
    this.name = name;
    this.likelyReleaseDayOfWeek = likelyReleaseDayOfWeek;
    this.likelyReleaseHourOfDay = likelyReleaseHourOfDay;
  }

  public String getName() {
    return name;
  }

  public int getLikelyReleaseDayOfWeek() {
    return likelyReleaseDayOfWeek;
  }

  public int getLikelyReleaseHourOfDay() {
    return likelyReleaseHourOfDay;
  }

  public int getWaitDurationSeconds() {
    DateTime now = DateTime.now(DateTimeZone.UTC);
    int nowFullWeekHours = convertToFullWeekHours(now.getDayOfWeek(), now.getHourOfDay());
    int likelyFullWeekHours = convertToFullWeekHours(likelyReleaseDayOfWeek, likelyReleaseHourOfDay);
    int differenceInHours = computeDifference(nowFullWeekHours, likelyFullWeekHours);
    if (differenceInHours < 2) {
      return 90;
    } else if (differenceInHours < 5) {
      return 210;
    }
    return 15 * 60;
  }

  private int computeDifference(int hours1, int hours2) {
    int maxValue = 7 * 24;
    int minValue = 0;
    if (hours1 + 24 >= maxValue && hours2 <= 24) {
      return maxValue - hours1 + hours2;
    }
    if (hours2 + 24 >= maxValue && hours1 <= 24) {
      return maxValue - hours2 + hours1;
    }
    return Math.abs(hours2 - hours1);
  }

  private int convertToFullWeekHours(int dayOfWeek, int hourOfDay) {
    int total = 0;
    switch(dayOfWeek) {
      case DateTimeConstants.SUNDAY:
        break;
      case DateTimeConstants.MONDAY:
        total += 24 * 1;
        break;
      case DateTimeConstants.TUESDAY:
        total += 24 * 2;
        break;
      case DateTimeConstants.WEDNESDAY:
        total += 24 * 3;
        break;
      case DateTimeConstants.THURSDAY:
        total += 24 * 4;
        break;
      case DateTimeConstants.FRIDAY:
        total += 24 * 5;
        break;
      case DateTimeConstants.SATURDAY:
        total += 24 * 6;
        break;
      default:
        throw new RuntimeException();
    }
    total += hourOfDay;
    return total;
  }
}
