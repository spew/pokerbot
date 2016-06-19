package org.poker.handler

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import com.github.nscala_time.time.Imports._
import it.jtomato.JTomato
import it.jtomato.gson.Movie
import org.poker.ProgramConfiguration
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

class RottenTomatoesMessageEventHandler(configuration: ProgramConfiguration) extends MessageEventHandler {
  val jTomato = new JTomato(configuration.rottenTomatoesApiKey.getOrElse(""))
  val startTime = DateTime.now

  override val helpMessage: Option[String] = Option("!rt <title>: send to channel rotten tomatoes ratings for <title>")

  override val messageMatchRegex: Regex = "^[!.](?i)((rt)|(rtomato)) ?(?<title>.*)".r

  override def onMessage(event: MessageReceivedEvent, firstMatch: Match): Unit = {
    val query = firstMatch.group(4)
    if (configuration.rottenTomatoesApiKey.isDefined) {
      val movies = new java.util.ArrayList[Movie]()
      val urlEncodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.name())
      val total = jTomato.searchMovie(query, movies, 1)
      if (total == 0) {
        event.getMessage.getChannel.sendMessage(s"no titles found for '${query}'")
      } else {
        val movie = movies.get(0)
        val message = s"${movie.title} | critics: ${movie.rating.criticsScore}% | audience: ${movie.rating.audienceScore}% | ${movie.links.alternate}"
        event.getMessage.getChannel.sendMessage(message)
      }
    } else {
      event.getMessage.getChannel.sendMessage("rotten tomatoes disabled: invalid api key")
    }
  }
}
