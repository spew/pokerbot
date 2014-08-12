package org.poker.handler

import org.poker.ProgramConfiguration

import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.{Channel, PircBotX}
import com.github.nscala_time.time.Imports._
import org.joda.time.format.PeriodFormatter
import org.poker.untapped._

class BeerMessageEventHandler(untappdClientId: String, untappdClientSecret: String, untappdAccessToken: String) extends MessageEventHandler {
  private val untappedClient = new UntappedClient(untappdClientId, untappdClientSecret, untappdAccessToken)
  override val helpMessage: Option[String] = Option("!beer: send untapped information about a beer to channel")
  override val messageMatchRegex: Regex = "^[!.](?i)beer? ?(?<query>.*)".r
  private val knownUsers: List[KnownUser] =
    (new KnownUser(39106, Seq("ctide", "chris", "tide"), "ctide"))::
      (new KnownUser(176916, Seq("sa-x", "matt"), "M4ttj0nes"))::
      (new KnownUser(931534, Seq("spew", "fud"), "spew"))::
      (new KnownUser(1468127, Seq("cl0ck", "clock", "mark"), "markmcgrail"))::
      (new KnownUser(1314112, Seq("tbs", "tbs_", "tom"), "tbs_"))::
      (new KnownUser(1490023, Seq("mike", "soul", "mylyons"), "mylons"))::
      (new KnownUser(1152859, Seq("brettkc", "brett", "bertkc", "idletom"), "brettkc"))::
      (new KnownUser(1152859, Seq("fourk", "james"), "Fourk"))::
      Nil
  private val userNameMap = knownUsers.map(u => u.aliases.map(a => (a, u))).flatten.toMap

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: Match): Unit = {
    val query = firstMatch.group(1).trim.toLowerCase()
    if (query.isEmpty) {
      sendTopCheckinToChannel(event.getChannel, untappedClient.recentFriendCheckins(), s"no recent checkins available")
    } else {
      userNameMap.get(query) match {
        case Some(user) => {
          sendTopCheckinToChannel(event.getChannel, untappedClient.recentCheckins(user.untappdUserName), s"no checkins available for '${query}'")
        }
        case None => {
          sendBeerInfoToChannel(event, query)
        }
      }
    }
  }

  private def sendTopCheckinToChannel(channel: Channel, checkinsResponse: UntappedResponse[CheckinsResponse], emptyMessage: String) = {
    if (checkinsResponse.response.checkins.items.isEmpty) {
      channel.send().message(emptyMessage)
    } else {
      val checkIn = checkinsResponse.response.checkins.items.head
      val beer = untappedClient.beerInfo(checkIn.beer.bid).response.beer
      val message = UntappdMessageFormatter.formatCheckinMessage(checkIn, beer)
      channel.send.message(message)
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

  private class KnownUser(val userId: Long, val aliases: Seq[String], val untappdUserName: String) {

  }
}