package org.poker.util

import com.github.nscala_time.time.Imports._
import org.ocpsoft.prettytime.PrettyTime

object RelativeTimeFormatter {
  def relativeToNow(dateTime: DateTime) = {
    val period = (dateTime to DateTime.now).toPeriod
    val durationSeconds = period.toDurationFrom(new DateTime()).getStandardSeconds
    val prettyTime = new PrettyTime(new java.util.Date(durationSeconds * 1000))
    prettyTime.format(new java.util.Date(0))
  }

  def relativeToDate(from: DateTime, to: DateTime) = {
    val period = (from to to).toPeriod
    val durationSeconds = period.toDurationFrom(new DateTime()).getStandardSeconds
    val p = new PrettyTime(from.toDate)
    p.format(to.toDate)
  }
}
