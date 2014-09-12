package org.poker.handler

import org.poker.util.RelativeTimeFormatter

import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.PircBotX
import com.github.nscala_time.time.Imports._
import org.poker.ProgramConfiguration
import org.poker.dota.KnownPlayer
import org.jsoup.Jsoup
import org.poker.steam.SteamClient
import org.poker.steam.dota.{Player, MatchDetails}
import org.ocpsoft.prettytime.PrettyTime
import java.util.concurrent.TimeUnit
import com.google.common.cache.{LoadingCache, CacheBuilder, CacheLoader}

class DotaMessageEventHandler(configuration: ProgramConfiguration) extends MessageEventHandler {
  val startTime = DateTime.now
  private val stevenPlayer = new KnownPlayer(28326143L, List("bunk", "steven"), true)
  val channelPlayers = getChannelPlayers()
  val steamClient = new SteamClient(configuration.steamApiKey.getOrElse(""))
  val idToPlayer = channelPlayers.map(kp => (kp.id, kp)).toMap
  val nameToPlayer = channelPlayers.map(kp => (kp.aliases.map(a => (a, kp)))).flatten.toMap

  override val helpMessage: Option[String] = Option("!dota <player>: send to channel stats about <player>, if no <player> then send to channel the last game played")

  override val messageMatchRegex: Regex = "^[!.](?i)((dota)|(dotabuff)) ?(?<player>.*)".r

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: Match): Unit = {
    if (configuration.steamApiKey.isDefined) {
      val playerName = firstMatch.group(4).toLowerCase
      if (playerName.isEmpty) {
        sendLatestMatch(event)
      } else {
        if (nameToPlayer.contains(playerName)) {
          val knownPlayer = nameToPlayer.get(playerName).get
          if (knownPlayer == stevenPlayer) {
            val xmasDay = new DateTime(2014, 12, 25, 0, 0)
            val now = DateTime.now
            val relativeTimeMsg = RelativeTimeFormatter.relativeToDate(now, xmasDay)
            val relativeDays = (now to xmasDay).toDuration.toStandardDays.getDays
            event.getChannel.send.message(s"${stevenPlayer.aliases.head} is retired from dota until ${relativeTimeMsg} (${relativeDays} days)")
          } else {
            sendIndividualPlayerStats(event, knownPlayer.id)
          }
        } else {
          event.getChannel.send.message(s"unknown player: ${playerName}")
        }
      }
    } else {
      event.getChannel.send.message("steam disabled: invalid api key")
    }
  }

  private def sendLatestMatch(event: MessageEvent[PircBotX]): Unit = {
    val m = findLatestMatch
    val knownPlayers = m.players.filter(p => p.account_id.isDefined && idToPlayer.contains(p.account_id.get))
    val win = (knownPlayers.head.player_slot < 128) == m.radiant_win
    val winMessage = if (win) "WIN" else "LOSS"
    val playerNames = knownPlayers.map(kp => this.getPlayerName(kp))
    val relativeDate = this.getFormattedRelativeFinishTime(m)
    var message = s"http://dotabuff.com/matches/${m.match_id} | ${relativeDate} | ${winMessage} for"
    if (playerNames.size > 1) {
      val andMessage = if (playerNames.size > 2) ", and " else " and "
      message += " " + playerNames.dropRight(1).mkString(", ") + andMessage + playerNames.last
    } else {
      message += " " + playerNames.head
    }
    event.getChannel.send.message(message)
  }

  val playerNameLoader = new CacheLoader[Long, String] {
    def load(playerId: Long) = {
      fetchPlayerName(playerId)
    }
  }
  val idToPlayerNameExpiring = CacheBuilder.newBuilder()
    .maximumSize(10000)
    .expireAfterWrite(30, TimeUnit.MINUTES)
    .build(playerNameLoader)
    .asInstanceOf[LoadingCache[Long, String]]

  private def getPlayerName(p: Player): String = {
    val playerId = new java.lang.Long(p.account_id.get)
    idToPlayerNameExpiring.get(playerId)
  }

  private def fetchPlayerName(accountId: Long) = {
    val url = s"http://dotabuff.com/players/${accountId}"
    val document = Jsoup.connect(url).get()
    val nameElement = document.select("div.content-header-title h1").first
    nameElement.textNodes().get(0).text()
  }

  private def findLatestMatch() = {
    val result = channelPlayers.filter(p => p.enabledForPing).map(p => steamClient.getLatestDotaMatches(p.id, 1)).flatten
    val sorted = result.sortBy(m => m.start_time).reverse
    val latestDetails = sorted.take(Math.min(3, sorted.size)).map(m => steamClient.getDotaMatchDetails(m.match_id))
    latestDetails.sortBy(m => m.start_time + m.duration).last
  }

  private def sendIndividualPlayerStats(event: MessageEvent[PircBotX], id: Long) {
    val url = s"http://dotabuff.com/players/${id}"
    val document = Jsoup.connect(url).get()
    val gamesWon = document.select("span.wins").first.text.replace(",", "").toInt
    val gamesLost = document.select("span.losses").first.text.replace(",", "").toInt
    val matches = this.getRecentResults(id, 10).reverse
    event.getChannel.send.message(url)
    if (matches.isEmpty) {
      event.getChannel.send.message("can't fetch latest results: data is private")
    } else {
      val winOrNot = matches.map(m => m.radiant_win == (m.players.filter(p => p.account_id.get == id)(0).player_slot < 128))
      val firstGameWin = winOrNot(0)
      val streakCount = winOrNot.takeWhile(r => r == winOrNot(0)).size;
      var streakType = "lost"
      if (firstGameWin) {
        streakType = "won"
      }
      val streakWins = winOrNot.filter(b => b).size
      val streakLosses = winOrNot.filter(b => !b).size
      val lastPlayed = this.getFormattedRelativeFinishTime(matches(0))
      val message = s"wins: ${gamesWon} | losses: ${gamesLost} | streak: ${streakType} ${streakCount} | last ten: ${streakWins}-${streakLosses} | last played: ${lastPlayed}"
      event.getChannel.send.message(message)
    }
  }

  private def getFormattedRelativeFinishTime(m: MatchDetails): String = {
    val date = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeZone.UTC).plusSeconds(m.start_time + m.duration)
    formatRelativeDate(date)
  }

  private def formatRelativeDate(date: DateTime): String = {
    val period = (date to new DateTime(DateTimeZone.UTC)).toPeriod()
    val duration = period.toDurationFrom(new DateTime())
    val seconds = duration.toStandardSeconds.getSeconds.toLong
    val prettyTime = new PrettyTime(new java.util.Date(seconds * 1000))
    prettyTime.format(new java.util.Date(0))
  }

  private def getRecentResults(playerId: Long, maxResults: Int): List[MatchDetails] = {
    val recentMatches = steamClient.getLatestDotaMatches(playerId, 2 * maxResults)
    var results = List[MatchDetails]();
    for (m <- recentMatches if results.size < maxResults) {
      val matchDetails = steamClient.getDotaMatchDetails(m.match_id)
      if (this.matchValid(matchDetails)) {
        results ::= matchDetails
      }
    }
    results
  }

  private def matchValid(m: MatchDetails): Boolean = {
    if (m.players.exists(p => !p.account_id.isDefined)) {
      false
    } else {
      if (m.duration < 10 * 60) {
        !m.players.dropWhile(p => p.leaver_status.getOrElse(0) != 2).isEmpty
      } else {
        true
      }
    }
  }

  private def getChannelPlayers(): List[KnownPlayer] = {
    new KnownPlayer(38926297L, List("whitey", "pete"), false)::
      new KnownPlayer(80342375L, List("bertkc", "brett", "bank", "gorby"), true)::
      new KnownPlayer(28308237L, List("mike"), true)::
      new KnownPlayer(10648475L, List("fud", "spew", "deathdealer69"), true)::
      stevenPlayer::
      new KnownPlayer(125412282L, List("mark", "clock", "cl0ck"), true)::
      new KnownPlayer(81397072L, List("clock2", "cl0ck2"), true)::
      new KnownPlayer(78932949L, List("muiy", "dank"), true)::
      new KnownPlayer(34117856L, List("viju", "vijal"), true)::
      new KnownPlayer(29508928L, List("sysm"), true)::
      new KnownPlayer(32387791L, List("ctide", "chris", "tide"), true)::
      new KnownPlayer(49941053L, List("abduhl", "jake"), true)::
      new KnownPlayer(32385879L, List("tbs", "tom"), true)::
      new KnownPlayer(40737752L, List("fourk"), true)::
      new KnownPlayer(12855832L, List("hed", "handsomehed", "xhedx"), true)::
      Nil
  }
}
