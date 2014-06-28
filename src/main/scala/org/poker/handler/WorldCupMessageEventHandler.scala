package org.poker.handler

import scala.util.matching.Regex
import scala.util.matching.Regex.{MatchData}

import org.poker.worldcup.{Team, Match, WorldCupClient}
import org.pircbotx.PircBotX
import org.pircbotx.hooks.events.MessageEvent
import org.joda.time.{DateTimeZone, DateTime}
import com.typesafe.scalalogging.slf4j.StrictLogging
import scala.collection.mutable.ListBuffer
import scala.collection.mutable
import com.github.nscala_time.time.Imports._
import org.poker.worldcup.Match
import org.poker.worldcup.Team
import org.ocpsoft.prettytime.PrettyTime

class WorldCupMessageEventHandler extends MessageEventHandler with StrictLogging {

  private val worldCupClient = new WorldCupClient()

  override val helpMessage: Option[String] = Option("!wc: send wc information about world cup to channel")

  override val messageMatchRegex: Regex = "^[!.](?i)wc? ?(?<query>.*)".r

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: scala.util.matching.Regex.Match): Unit = {
    val query = firstMatch.group(1).trim
    event.getChannel.send.message(getChannelMessage(query))
  }

  private def getChannelMessage(query: String) = {
    query match {
      case "help" => getUsage
      case "tomorrow" => getTomorrowsMatchesMessage()
      case _ => getTodaysMatchesMessage()
    }
  }

  private def getTodaysMatchesMessage() = {
    val matches = worldCupClient.getTodaysMatches()
    formatMatches(matches)
  }

  private def getTomorrowsMatchesMessage() = {
    val matches = worldCupClient.getTomorrowsMatches()
    formatMatches(matches)
  }

  private def formatMatches(matches: List[Match]) = {
    val messages = mutable.MutableList[String]()
    for (m <- matches) {
      if (isMatchValid(m)) {
        messages += formatMatch(m)
      }
    }
    messages.mkString(" | ")
  }

  private def isMatchValid(m: Match) = {
    !(m.away_team.code == "TBD" && m.home_team.code == "TBD")
  }

  private def formatMatch(m: Match) = {
    val timeMessage = m.status match {
      case "in progress" => "LIVE"
      case "completed" => "FINAL"
      case "future" => formatRelativeTime(m.datetime)
      case _ => formatRelativeTime(m.datetime)
    }
    val showGoals = shouldShowGoals(m)
    s"${formatTeam(m.home_team, showGoals)} vs ${formatTeam(m.away_team, showGoals)} (${timeMessage})"
  }

  private def shouldShowGoals(m: Match) = {
    m.status match {
      case "in progress" => true
      case "completed" => true
      case "future" => false
      case _ => false
    }
  }

  private def formatTeam(t: Team, showGoals: Boolean) = {
    if (showGoals) {
      s"${t.code} ${t.goals.getOrElse("0")}"
    } else {
      s"${t.code}"
    }
  }

  // TODO: push this stuff down into a trait that can be re-used
  private def formatRelativeTime(d: DateTime) = {
    val now = DateTime.now()
    val period = if (d.isBefore(now)) (d to now).toPeriod else (now to d).toPeriod
    if (d.isBefore(now)) {
      val prettyTime = new PrettyTime(new java.util.Date(period.toStandardDuration.getStandardSeconds * 1000))
      val formattedDate = prettyTime.format(new java.util.Date(0))
      formattedDate
    } else {
      val prettyTime = new PrettyTime(new java.util.Date(0))
      val formattedDate = prettyTime.format(new java.util.Date(period.toStandardDuration.getStandardSeconds * 1000))
      formattedDate
    }
  }

  private def getUsage() = {
    "usage: !wc <command> | available commands: { today, tomorrow, help }"
  }
}
