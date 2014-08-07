package org.poker.util

import com.github.nscala_time.time.Imports._
import org.ocpsoft.prettytime.PrettyTime

object RelativeTimeFormatter {
  def relativeToNow(dateTime: DateTime) = {
    val period = (dateTime to DateTime.now).toPeriod
    val prettyTime = new PrettyTime(new java.util.Date(period.toStandardDuration.getStandardSeconds * 1000))
    prettyTime.format(new java.util.Date(0))
  }
}
