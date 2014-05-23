package org.poker.handler

import scala.util.matching.Regex
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.PircBotX
import scala.util.matching.Regex.Match
import org.poker.twitch.{Game, TwitchClient, StreamsResponse}
import org.poker.ProgramConfiguration

class StreamsMessageEventHandler(configuration: ProgramConfiguration) extends MessageEventHandler {
  val baseUrl = "https://api.twitch.tv/kraken/"
  val twitchClient = new TwitchClient(configuration.twitchClientId.getOrElse(""))

  override val helpMessage: Option[String] = Option("!streams <query>: send to channel the top twitch search results for <query>")

  override val messageMatchRegex: Regex = "[!.](?i)streams? ?(?<query>.*)".r

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: Match): Unit = {
    if (configuration.twitchClientId.isDefined) {
      val query = firstMatch.group(1)
      handleQuery(event, query)
    } else {
      event.getChannel.send.message("twitch disabled: no client id")
    }
  }

  private def queryGames(query: String): List[Game] = {
    if (query.isEmpty) {
      twitchClient.queryGames("Dota 2").games
    } else {
      twitchClient.queryGames(query).games
    }
  }

  private def handleQuery(event: MessageEvent[PircBotX], query: String) {
    val games = this.queryGames(query)
    if (games.isEmpty) {
      event.getChannel.send.message(s"no twitch games found for query: '$query'")
    } else {
      val game = games(0)
      val streamsResponse = twitchClient.queryStreams(game.name, 3)
      if (streamsResponse.streams.isEmpty) {
        event.getChannel.send().message(s"sorry, game '${game.name}' is dead")
      } else {
        for (stream <- streamsResponse.streams) {
          val message = s"${stream.channel.display_name} | ${stream.viewers} | ${stream.channel.url}/popout"
          event.getChannel.send.message(message)
        }
      }
    }
  }
}
