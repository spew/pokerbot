package org.poker.handler


import org.pircbotx.PircBotX
import org.pircbotx.hooks.events.MessageEvent
import org.poker.imdb.{NotFoundResult, FoundResult, ImdbClient, ImdbMessageFormatter}

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

class ImdbMessageEventHandler extends MessageEventHandler {
  override val helpMessage: Option[String] = Option("!imdb <query>: send to channel IMDB film info")
  override val messageMatchRegex: Regex = "^[!.](?i)(imdb) ?(?<query>.*)".r
  val imdbClient = new ImdbClient()

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: Match): Unit = {
    val query = Option(firstMatch.group(2).toLowerCase)
    val message = formatMessage(query)
    event.getChannel.send.message(message)
  }

  private def formatMessage(query: Option[String]) = {
    if (query.isDefined) {
      val imdbResult = imdbClient.getFilmInfo(query.get)
      ImdbMessageFormatter.format(imdbResult)
    } else {
      s"usage: ${helpMessage}"
    }
  }
}
