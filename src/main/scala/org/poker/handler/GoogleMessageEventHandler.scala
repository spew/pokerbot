package org.poker.handler

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.{HttpRequest, HttpRequestInitializer, HttpTransport}
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson.JacksonFactory
import com.google.api.services.customsearch.Customsearch
import org.poker.ProgramConfiguration
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

class GoogleMessageEventHandler(configuration: ProgramConfiguration) extends MessageEventHandler {
  val googleClient = createClient()

  override val helpMessage: Option[String] = Option("!google <query>: send to channel the top google search result for <query>")

  override val messageMatchRegex: Regex = "^[!.](?i)google (?<query>.*)".r

  override def onMessage(event: MessageReceivedEvent, firstMatch: Match): Unit = {
    val query = firstMatch.group(1)
    if (configuration.googleSearchApiKey.isDefined && configuration.googleSearchCxKey.isDefined) {
      val list = googleClient.cse().list(query)
      list.setCx(configuration.googleSearchCxKey.get)
      list.setKey(configuration.googleSearchApiKey.get)
      val results = list.execute().getItems
      if (results.isEmpty) {
        event.getMessage.getChannel.sendMessage(s"nothing found by google for '$query'")
      } else {
        val first = results.get(0)
        event.getMessage.getChannel.sendMessage(first.getLink)
        event.getMessage.getChannel.sendMessage(first.getTitle)
      }
    } else {
      event.getMessage.getChannel.sendMessage("can't google, api-key and cx-key are required")
    }
  }

  private def createClient() : Customsearch = {
    val JSON_FACTORY: JsonFactory = new JacksonFactory
    var httpTransport: HttpTransport = null
    val httpRequestInitializer: HttpRequestInitializer = new HttpRequestInitializer {
      def initialize(httpRequest: HttpRequest) {
      }
    }
    var client: Customsearch.Builder = null
    httpTransport = GoogleNetHttpTransport.newTrustedTransport
    client = new Customsearch.Builder(httpTransport, JSON_FACTORY, httpRequestInitializer).setApplicationName("#poker")
    client.build
  }
}
