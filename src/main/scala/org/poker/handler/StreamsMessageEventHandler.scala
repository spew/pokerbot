package org.poker.handler

import org.poker.ProgramConfiguration
import org.poker.twitch.{Game, TwitchClient}
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

class StreamsMessageEventHandler(configuration: ProgramConfiguration) extends MessageEventHandler {
  val baseUrl = "https://api.twitch.tv/kraken/"
  val twitchClient = new TwitchClient(configuration.twitchClientId.getOrElse(""))

  override val helpMessage: Option[String] = Option("!streams <query>: send to channel the top twitch search results for <query>")

  override val messageMatchRegex: Regex = "^[!.](?i)streams? ?(?<query>.*)".r

  override def onMessage(event: MessageReceivedEvent, firstMatch: Match): Unit = {
    if (configuration.twitchClientId.isDefined) {
      val query = firstMatch.group(1)
      handleQuery(event, query)
    } else {
      event.getMessage.getChannel.sendMessage("twitch disabled: no client id")
    }
  }

  private def queryGames(query: String): List[Game] = {
    if (query.isEmpty) {
      twitchClient.queryGames("Dota 2").games
    } else {
      twitchClient.queryGames(query).games
    }
  }

  private def handleQuery(event: MessageReceivedEvent, query: String) {
    val games = this.queryGames(query)
    if (games.isEmpty) {
      event.getMessage.getChannel.sendMessage(s"no twitch games found for query: '$query'")
    } else {
      val game = games(0)
      val streamsResponse = twitchClient.queryStreams(game.name, 3)
      if (streamsResponse.streams.isEmpty) {
        event.getMessage.getChannel.sendMessage(s"sorry, game '${game.name}' is dead")
      } else {
        event.getMessage.getChannel.sendMessage(s"streams for '${game.name}'")
        for (stream <- streamsResponse.streams) {
          val message = s"${stream.channel.display_name} | ${stream.viewers} | ${stream.channel.url}/popout"
          event.getMessage.getChannel.sendMessage(message)
        }
      }
    }
  }
}
