package org.poker.handler

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

import org.poker.worldcup.WorldCupClient
import org.pircbotx.PircBotX
import org.pircbotx.hooks.events.MessageEvent
import org.poker.untapped.CheckinCount
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
      event.getChannel.send.message("usage: !wc [current]")
    } else {
      sendWorldCupInfoToChannel(event, query)
    }
  }

  private def sendWorldCupInfoToChannel(event: MessageEvent[PircBotX], query: String) = {
    def sendError = event.getChannel.send.message(s"nothing for '${query}'")
    query match {
      case "current" => {
        val searchResponse = worldCupClient.current
        if (searchResponse.isEmpty) {
          sendError
        }
        else {
          val buf = new ListBuffer[String]
          for (mtch <- searchResponse) {
            mtch.n_AwayGoals match {
              case Some(i) => buf.append(s"${mtch.c_AwayNatioShort} ${mtch.n_AwayGoals.get} - ${mtch.n_HomeGoals.get} ${mtch.c_HomeNatioShort} : ${mtch.c_MatchStatusShort}")
              case None => {
                val tmp = mtch.c_MatchDayDate.split(':').toList.take(2)
                val tmp2 = tmp(0).split('T')
                val date = tmp2(0).replace("2014-", "")
                val time = s"${tmp2(1)}:${tmp(1)}"
                buf.append(s"${mtch.c_AwayNatioShort} vs ${mtch.c_HomeNatioShort} @ ${date} ${time} ")
              }
            }
          }
          event.getChannel.send.message( buf.mkString(" // ") )
        }

      }
      case _ => sendError
    }
  }

}
