package org.poker.handler

import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.PircBotX
import com.github.nscala_time.time.Imports._
import org.joda.time.format.PeriodFormatter
import org.poker.untapped.{SearchResponse, UntappedResponse, CheckinCount, UntappedClient}

class BeerMessageEventHandler(clientId: String, clientSecret: String) extends MessageEventHandler {
  private val untappedClient = new UntappedClient(clientId, clientSecret)

  override val helpMessage: Option[String] = Option("!beer: send untapped information about a beer to channel")

  override val messageMatchRegex: Regex = "[!.](?i)beer? ?(?<query>.*)".r

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: Match): Unit = {
    val query = firstMatch.group(1).trim
    if (query.isEmpty) {
      event.getChannel.send.message("usage: !beer <query>")
    } else {
      sendBeerInfoToChannel(event, query)
    }
  }

  private def sendBeerInfoToChannel(event: MessageEvent[PircBotX], query: String) = {
    val searchResponse = untappedClient.beerSearch(query, CheckinCount())
    if (searchResponse.response.beers.items.isEmpty) {
      event.getChannel.send.message(s"no beers found for '${query}'")
    } else {
      val firstResult = searchResponse.response.beers.items.head
      val beerInfoResponse = untappedClient.beerInfo(firstResult.beer.bid)
      val beer = beerInfoResponse.response.beer
      val untappedUrl = s"https://untappd.com/b/${beer.beer_slug}/${beer.bid}"
      event.getChannel.send.message(s"${beer.beer_name} | rating: ${beer.rating_score} | style: ${beer.beer_style} | abu: ${beer.beer_abv} | ibu: ${beer.beer_ibu} | ${untappedUrl}")
    }
  }
}
