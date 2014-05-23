package org.poker.handler

import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.PircBotX
import com.github.nscala_time.time.Imports._
import org.joda.time.format.{PeriodFormatterBuilder, PeriodFormatter}

class UptimeMessageEventHandler extends MessageEventHandler {
  val startTime = DateTime.now

  override val helpMessage: Option[String] = Option("!uptime: send bot uptime to channel")

  override val messageMatchRegex: Regex = "[.!](?i)uptime".r

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: Match): Unit = {
    val now = DateTime.now
    val interval = startTime to now
    val period = interval.toPeriod()
    val formatter: PeriodFormatter = new PeriodFormatterBuilder()
      .appendYears.appendSeparator(" years, ")
      .appendMonths.appendSeparator(" months, ")
      .appendDays.appendSeparator(" days, ")
      .minimumPrintedDigits(2)
      .appendHours
      .appendSeparator(":")
      .printZeroAlways
      .appendMinutes
      .appendSeparator(":")
      .appendSeconds
      .toFormatter
    val message = formatter.print(period)
    event.getChannel.send().message("uptime: " + message)
  }
}
