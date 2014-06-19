package org.poker.handler

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

import org.poker.worldcup.{MatchData, GroupData, WorldCupClient}
import org.pircbotx.PircBotX
import org.pircbotx.hooks.events.MessageEvent
import org.joda.time.{DateTimeZone, DateTime}
import com.typesafe.scalalogging.slf4j.StrictLogging
import scala.collection.mutable.ListBuffer


/**
 * Created by mylons on 6/17/14.
 */
class WorldCupMessageEventHandler extends MessageEventHandler with StrictLogging {

  private val worldCupClient = new WorldCupClient()

  override val helpMessage: Option[String] = Option("!wc: send wc information about world cup to channel")

  override val messageMatchRegex: Regex = "^[!.](?i)wc? ?(?<query>.*)".r

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: Match): Unit = {
    val query = firstMatch.group(1).trim
    if (query.isEmpty) {
      event.getChannel.send.message(matchString(worldCupClient.today))
    } else {
      sendWorldCupInfoToChannel(event, query)
    }
  }
  private def matchString( matches: List[MatchData] ) = {
    val buf = new ListBuffer[String]
    for (mtch <- matches) {
      mtch.n_AwayGoals match {
        case Some(i) =>
          buf.append(s"${mtch.c_AwayNatioShort} ${mtch.n_AwayGoals.get} - ${mtch.n_HomeGoals.get} ${mtch.c_HomeNatioShort} : ${mtch.c_MatchStatusShort}")
        case None => {
          val date = new DateTime(mtch.c_MatchDayDate).withZone(DateTimeZone.forID("America/Sao_Paulo"))
          buf.append(s"${mtch.c_AwayNatioShort} vs ${mtch.c_HomeNatioShort} @ ${date.monthOfYear().get}-${date.dayOfMonth().get} ${date.toLocalTime.toString("HH:mm")}")
        }
      }
    }
    buf.mkString(" | ")
  }

  private def sendWorldCupInfoToChannel(event: MessageEvent[PircBotX], query: String) = {

    def sendError = event.getChannel.send.message("usage: !wc [current today help]")

    val searchResponse: List[MatchData] = query match {
      case "current" => worldCupClient.current
      case "today" => worldCupClient.today
      case "help" => worldCupClient.help
    }
    if (searchResponse.isEmpty) {
      sendError
    }
    else {
      event.getChannel.send.message(matchString(searchResponse))
    }
  }

}
