package org.poker.handler


import org.pircbotx.PircBotX
import org.pircbotx.hooks.events.MessageEvent
import org.poker.imdb.{NotFoundResult, FoundResult, ImdbClient}

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

class ImdbMessageEventHandler extends MessageEventHandler {
  override val helpMessage: Option[String] = Option("!imdb <query>: send to channel IMDB film info")
  override val messageMatchRegex: Regex = "^[!.](?i)(imdb) ?(?<query>.*)".r
  val imdbClient = new ImdbClient()

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: Match): Unit = {
    val query = Option(firstMatch.group(2).toLowerCase)
    if(query.isDefined){
      sendImdbMessage(event, query.get)
    }
  }

  private def sendImdbMessage(event: MessageEvent[PircBotX], title: String): Unit = {
    val imdbResult = imdbClient.getFilmInfo(title)
    imdbResult match {
      case FoundResult(title, year, parentalRating, released, runtime, rating, genre) =>
        val message = s"${title} | Rating: ${rating} | Genre: ${genre} |  Rated: ${parentalRating} | Released: ${released}"
        event.getChannel.send.message(message)
      case NotFoundResult(response, error) =>
        val message = s"Film not found."
        event.getChannel.send.message(message)
    }
  }
}
