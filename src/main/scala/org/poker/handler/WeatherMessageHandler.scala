package org.poker.handler

import org.pircbotx.PircBotX
import org.pircbotx.hooks.events.MessageEvent
import org.poker.ProgramConfiguration
import org.poker.yahoo.WeatherClient

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

class WeatherMessageHandler(val configuration: ProgramConfiguration) extends MessageEventHandler {
  private val weatherClient = new WeatherClient(configuration.yahooConsumerKey.get, configuration.yahooConsumerSecret.get)
  override val messageMatchRegex: Regex = "^[!.](?i)((weather)|(w)) (?<query>.*)".r
  override val helpMessage: Option[String] = Option("!weather <query>: send current weather information for <query> to channel")

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: Match): Unit = {
    val query = firstMatch.group(4).trim
    if (query.isEmpty) {
      event.getChannel.send().message("usage: !weather <query>")
    } else {
      val wr = weatherClient.getResults(query)
      val message = s"${wr.channel.item.title}: ${wr.channel.item.condition.text}, ${wr.channel.item.condition.temp} ${wr.channel.units.temperature}"
      // for some reason yahoo returns am/pm in lowercase
      val newMessage = message.replace(" am ", " AM ").replace(" pm ", " PM ")
      event.getChannel.send().message(newMessage);
    }
  }
}
