package org.poker.scc

import org.joda.time.DateTimeConstants
import com.github.nscala_time.time.Imports.DateTime
import scala.concurrent.duration._

class SceneShow(val name: String, val releaseDayOfWeek: Int, val releaseHourOfDay: Int) {

  def currentPollWait(): FiniteDuration = {
    val now = DateTime.now
    val nowFullWeekHours = convertToFullWeekHours(now.getDayOfWeek, now.getHourOfDay)
    val likelyFullWeekHours = convertToFullWeekHours(releaseDayOfWeek, releaseHourOfDay)
    val diffHours = computeDifference(nowFullWeekHours, likelyFullWeekHours)
    if (diffHours < 2) {
      90.seconds
    } else if (diffHours < 5) {
      210.seconds
    } else {
      15.minutes
    }
  }

  private def computeDifference(hours1: Int, hours2: Int): Int = {
    val maxValue = 7 * 24
    if (hours1 + 24 >= maxValue && hours2 <= 24) {
      return maxValue - hours1 + hours2
    }
    else if (hours2 + 24 >= maxValue && hours1 <= 24) {
      return maxValue - hours2 + hours1
    }
    return Math.abs(hours2 - hours1)
  }

  private def convertToFullWeekHours(dayOfWeek: Int, hourOfDay: Int): Int = {
    hourOfDay + (dayOfWeek match {
      case DateTimeConstants.SUNDAY => 0
      case DateTimeConstants.MONDAY => 24 * 1
      case DateTimeConstants.TUESDAY => 24 * 2
      case DateTimeConstants.WEDNESDAY => 24 * 3
      case DateTimeConstants.THURSDAY => 24 * 4
      case DateTimeConstants.FRIDAY => 24 * 5
      case DateTimeConstants.SATURDAY => 24 * 6
      case _ => throw new Exception("Unknown day")
    })
  }

}
