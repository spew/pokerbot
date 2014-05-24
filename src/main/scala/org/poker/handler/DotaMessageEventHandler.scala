package org.poker.handler

import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.PircBotX
import com.github.nscala_time.time.Imports._
import org.joda.time.format.{PeriodFormatterBuilder, PeriodFormatter}
import org.poker.ProgramConfiguration
import org.poker.dota.KnownPlayer
import org.jsoup.Jsoup
import org.poker.steam.SteamClient
import org.poker.steam.dota.MatchDetails
import org.poker.steam.dota.Player
import org.ocpsoft.prettytime.PrettyTime
import scala.collection.mutable.Set
import scala.collection._

class DotaMessageEventHandler(configuration: ProgramConfiguration) extends MessageEventHandler {
  val startTime = DateTime.now
  val channelPlayers = getChannelPlayers()
  val steamClient = new SteamClient(configuration.steamApiKey.getOrElse(""))
  val idToPlayer = channelPlayers.map(kp => (kp.id, kp)).toMap
  val nameToPlayer = channelPlayers.map(kp => (kp.aliases.map(a => (a, kp)))).flatten.toMap
  val idToPlayerName = mutable.Map[Long, String]()

  override val helpMessage: Option[String] = Option("!dota <player>: send to channel stats about <player>, if no <player> then send to channel the last game played")

  override val messageMatchRegex: Regex = "[!.](?i)((dota)|(dotabuff)) ?(?<player>.*)".r

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: Match): Unit = {
    if (configuration.steamApiKey.isDefined) {
      val playerName = firstMatch.group(4)
      if (playerName.isEmpty) {
        sendLatestMatch(event)
      } else {
        if (nameToPlayer.contains(playerName)) {
          val knownPlayer = nameToPlayer.get(playerName).get
          sendIndividualPlayerStats(event, knownPlayer.id)
        } else {
          event.getChannel.send.message(s"unknown player: ${playerName}")
        }
      }
    } else {
      event.getChannel.send.message("steam disabled: invalid api key")
    }
  }

  private def sendLatestMatch(event: MessageEvent[PircBotX]): Unit = {
    val latestMatch = findLatestMatch()
    val m = steamClient.getDotaMatchDetails(latestMatch.match_id)
    val knownPlayers = m.players.filter(p => idToPlayer.contains(p.account_id))
    val win = (knownPlayers.head.player_slot < 128) == m.radiant_win
    val winMessage = if (win) "WIN" else "LOSS"
    val playerNames = knownPlayers.map(kp => this.getPlayerName(kp))
    val relativeDate = this.getFormattedRelativeFinishTime(m)
    var message = s"latest: http://dotabuff.com/matches/${m.match_id} | ${relativeDate} | ${winMessage} for "
    if (playerNames.size > 1) {
      val andMessage = if (playerNames.size > 2) ", and " else " and "
      message += " " + playerNames.dropRight(1).mkString(", ") + andMessage + playerNames.last
    } else {
      message += " " + playerNames.head
    }
    event.getChannel.send.message(message)
  }

  private def getPlayerName(p: Player): String = {
    if (!idToPlayerName.contains(p.account_id)) {
      val url = s"http://dotabuff.com/players/${p.account_id}"
      val document = Jsoup.connect(url).get()
      val nameElement = document.select("div.content-header-title h1").first
      idToPlayerName += p.account_id -> nameElement.text
    }
    idToPlayerName.get(p.account_id).get
  }

  private def findLatestMatch(): org.poker.steam.dota.Match = {
    val matches = channelPlayers.map(p => steamClient.getLatestDotaMatches(p.id, 1)).flatten
    val sorted = matches.sortBy(m => m.start_time)
    sorted.last
  }

  private def sendIndividualPlayerStats(event: MessageEvent[PircBotX], id: Long) {
    val url = s"http://dotabuff.com/players/${id}"
    val document = Jsoup.connect(url).get()
    val gamesWon = document.select("span.won").first.text.toInt
    val gamesLost = document.select("span.lost").first.text.toInt
    val matches = this.getRecentResults(id, 10).reverse
    val winOrNot = matches.map(m => m.radiant_win == (m.players.filter(p => p.account_id == id)(0).player_slot < 128))
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
    event.getChannel.send.message(url)
    event.getChannel.send.message(message)
  }

  private def getFormattedRelativeFinishTime(m: MatchDetails): String = {
    val date = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeZone.UTC).plusSeconds(m.start_time + m.duration)
    formatRelativeDate(date)
  }

  private def formatRelativeDate(date: DateTime): String = {
    val period = (date to new DateTime(DateTimeZone.UTC)).toPeriod()
    val prettyTime = new PrettyTime(new java.util.Date(period.toStandardDuration.getStandardSeconds * 1000))
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
    if (m.duration < 10 * 60) {
      !m.players.dropWhile(p => p.leaver_status.getOrElse(0) != 2).isEmpty
    } else {
      true
    }
  }

  private def getChannelPlayers(): List[KnownPlayer] = {
    new KnownPlayer(38926297L, List("whitey", "pete"))::
      new KnownPlayer(80342375L, List("bertkc", "brett", "bank", "gorby"))::
      new KnownPlayer(28308237L, List("mike"))::
      new KnownPlayer(10648475L, List("fud", "spew", "deathdealer69"))::
      new KnownPlayer(28326143L, List("steven", "bunk"))::
      new KnownPlayer(125412282L, List("mark", "clock", "cl0ck"))::
      new KnownPlayer(78932949L, List("muiy", "dank"))::
      new KnownPlayer(34117856L, List("viju", "vijal"))::
      new KnownPlayer(29508928L, List("sysm"))::
      Nil
  }
}
