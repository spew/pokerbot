package org.poker.handler


import org.poker.imdb.{ImdbClient, ImdbMessageFormatter}
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

class ImdbMessageEventHandler extends MessageEventHandler {
  override val helpMessage: Option[String] = Option("!imdb <query>: send to channel IMDB film info")
  override val messageMatchRegex: Regex = "^[!.](?i)(imdb) ?(?<query>.*)".r
  val imdbClient = new ImdbClient()

  override def onMessage(event: MessageReceivedEvent, firstMatch: Match): Unit = {
    val query = Option(firstMatch.group(2).toLowerCase)
    val message = formatMessage(query)
    event.getMessage.getChannel.sendMessage(message)
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
