package org.poker.handler

import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.PircBotX
import com.github.nscala_time.time.Imports._
import org.poker.ProgramConfiguration
import it.jtomato.JTomato
import it.jtomato.gson.Movie

class RottenTomatoesMessageEventHandler(configuration: ProgramConfiguration) extends MessageEventHandler {
  val jTomato = new JTomato(configuration.rottenTomatoesApiKey.getOrElse(""))
  val startTime = DateTime.now

  override val helpMessage: Option[String] = Option("!rt <title>: send to channel rotten tomatoes ratings for <title>")

  override val messageMatchRegex: Regex = "[!.](?i)((rt)|(rtomato)) ?(?<title>.*)".r

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: Match): Unit = {
    val query = firstMatch.group(4)
    if (configuration.rottenTomatoesApiKey.isDefined) {
      val movies = new java.util.ArrayList[Movie]()
      val total = jTomato.searchMovie(query, movies, 1)
      if (total == 0) {
        event.getChannel.send.message(s"no titles found for '${query}'")
      } else {
        val movie = movies.get(0)
        val message = s"${movie.title} | critics: ${movie.rating.criticsScore}% | audience: ${movie.rating.audienceScore}% | ${movie.links.alternate}"
        event.getChannel.send.message(message)
      }
    } else {
      event.getChannel.send.message("rotten tomatoes disabled: invalid api key")
    }
  }
}
