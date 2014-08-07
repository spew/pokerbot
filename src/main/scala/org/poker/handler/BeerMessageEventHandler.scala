package org.poker.handler

import org.poker.ProgramConfiguration

import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.PircBotX
import com.github.nscala_time.time.Imports._
import org.joda.time.format.PeriodFormatter
import org.poker.untapped.{SearchResponse, UntappedResponse, CheckinCount, UntappedClient}

class BeerMessageEventHandler(untappdClientId: String, untappdClientSecret: String, untappdAccessToken: String) extends MessageEventHandler {
  private val untappedClient = new UntappedClient(untappdClientId, untappdClientSecret, untappdAccessToken)

  override val helpMessage: Option[String] = Option("!beer: send untapped information about a beer to channel")

  override val messageMatchRegex: Regex = "^[!.](?i)beer? ?(?<query>.*)".r

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: Match): Unit = {
    val query = firstMatch.group(1).trim
    if (query.isEmpty) {
      val response = untappedClient.recentFriendCheckins()
      val checkins = response.response.checkins.items
      val lastCheckin = checkins.head
      val beerInfo = untappedClient.beerInfo(lastCheckin.beer.bid)
      val rating = formatRating(beerInfo.response.beer.rating_score)
      val url = s"http://untappd.com/user/${lastCheckin.user.user_name}/checkin/${lastCheckin.checkin_id}"
      val venueMessage = if (lastCheckin.venue.isDefined) s"at '${lastCheckin.venue.get.venue_name}' " else ""
      val message = s"${lastCheckin.user.user_name} just rated '${lastCheckin.beer.beer_name}' ${lastCheckin.rating_score}/5 (avg ${rating}) ${venueMessage}| ${url}"
      event.getChannel.send.message(message)
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
      val rating = formatRating(beer.rating_score)
      event.getChannel.send.message(s"${beer.beer_name} | ${rating}/5.0 | style: ${beer.beer_style} | abv: ${beer.beer_abv} | ibu: ${beer.beer_ibu} | ${untappedUrl}")
    }
  }

  private def formatRating(rating: Double) = {
    f"${rating}%1.1f"
  }
}