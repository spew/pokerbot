package org.poker.handler

import org.poker.untapped._
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

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
      (new KnownUser(1152859, Seq("muiy", "matt", "dank"), "Muiy"))::
      Nil
  private val userNameMap = knownUsers.map(u => u.aliases.map(a => (a, u))).flatten.toMap

  override def onMessage(event: MessageReceivedEvent, firstMatch: Match): Unit = {
    val query = firstMatch.group(1).trim.toLowerCase()
    if (query.isEmpty) {
      sendTopCheckinToChannel(event.getMessage.getChannel, untappedClient.recentFriendCheckins(), s"no recent checkins available")
    } else {
      userNameMap.get(query) match {
        case Some(user) => {
          sendTopCheckinToChannel(event.getMessage.getChannel, untappedClient.recentCheckins(user.untappdUserName), s"no checkins available for '${query}'")
        }
        case None => {
          sendBeerInfoToChannel(event, query)
        }
      }
    }
  }

  private def sendTopCheckinToChannel(channel: IChannel, checkinsResponse: UntappedResponse[CheckinsResponse], emptyMessage: String) = {
    if (checkinsResponse.response.checkins.items.isEmpty) {
      channel.sendMessage(emptyMessage)
    } else {
      val checkIn = checkinsResponse.response.checkins.items.head
      val beer = untappedClient.beerInfo(checkIn.beer.bid).response.beer
      val message = UntappdMessageFormatter.formatCheckin(checkIn, beer)
      channel.sendMessage(message)
    }
  }

  private def sendBeerInfoToChannel(event: MessageReceivedEvent, query: String) = {
    val searchResponse = untappedClient.beerSearch(query, CheckinCount)
    if (searchResponse.response.beers.items.isEmpty) {
      event.getMessage.getChannel.sendMessage(s"no beers found for '${query}'")
    } else {
      val beerInfoResponse = untappedClient.beerInfo(searchResponse.response.beers.items.head.beer.bid)
      val message = UntappdMessageFormatter.formatBeer(beerInfoResponse.response.beer)
      event.getMessage.getChannel.sendMessage(message)
    }
  }

  private class KnownUser(val userId: Long, val aliases: Seq[String], val untappdUserName: String) {

  }
}