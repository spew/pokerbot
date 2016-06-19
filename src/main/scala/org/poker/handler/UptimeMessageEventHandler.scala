package org.poker.handler

import com.github.nscala_time.time.Imports._
import org.joda.time.format.{PeriodFormatter, PeriodFormatterBuilder}
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

class UptimeMessageEventHandler extends MessageEventHandler {
  val startTime = DateTime.now
  override val helpMessage: Option[String] = Option("!uptime: send bot uptime to channel")
  override val messageMatchRegex: Regex = "^[.!](?i)uptime".r

  override def onMessage(event: MessageReceivedEvent, firstMatch: Match): Unit = {
    val message = formatMessage()
    event.getMessage.getChannel.sendMessage(message)
  }

  private def formatMessage() = {
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
    "uptime: " + formatter.print(period)
  }
}
