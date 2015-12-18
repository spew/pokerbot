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
      (new KnownUser(1152859, Seq("muiy", "matt"), "Muiy"))::
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
      val message = UntappdMessageFormatter.formatCheckin(checkIn, beer)
      channel.send.message(message)
    }
  }

  private def sendBeerInfoToChannel(event: MessageEvent[PircBotX], query: String) = {
    val searchResponse = untappedClient.beerSearch(query, CheckinCount)
    if (searchResponse.response.beers.items.isEmpty) {
      event.getChannel.send.message(s"no beers found for '${query}'")
    } else {
      val beerInfoResponse = untappedClient.beerInfo(searchResponse.response.beers.items.head.beer.bid)
      val message = UntappdMessageFormatter.formatBeer(beerInfoResponse.response.beer)
      event.getChannel.send.message(message)
    }
  }

  private class KnownUser(val userId: Long, val aliases: Seq[String], val untappdUserName: String) {

  }
}