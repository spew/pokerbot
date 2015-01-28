package org.poker.handler

import org.poker.util.RelativeTimeFormatter

import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.PircBotX
import com.github.nscala_time.time.Imports._
import org.poker.ProgramConfiguration
import org.poker.dota.{MatchFormatter, LatestMatchFinder, KnownPlayers, KnownPlayer}
import org.jsoup.Jsoup
import org.poker.steam.SteamClient
import org.poker.steam.dota.{Player, MatchDetails}
import org.ocpsoft.prettytime.PrettyTime
import java.util.concurrent.TimeUnit
import com.google.common.cache.{LoadingCache, CacheBuilder, CacheLoader}

class DotaMessageEventHandler(configuration: ProgramConfiguration) extends MessageEventHandler {
  val startTime = DateTime.now
  val channelPlayers = KnownPlayers.all
  val steamClient = new SteamClient(configuration.steamApiKey.getOrElse(""))
  val idToPlayer = channelPlayers.map(kp => (kp.id, kp)).toMap
  val nameToPlayer = channelPlayers.map(kp => (kp.aliases.map(a => (a, kp)))).flatten.toMap
  private val latestMatchFinder = new LatestMatchFinder(steamClient)
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
          if (knownPlayer == KnownPlayers.steven) {
            val now = DateTime.now
            val xmasDay = new DateTime(2014, 12, 25, 8, 0)
            if (now.isBefore(xmasDay)) {
              val relativeTimeMsg = RelativeTimeFormatter.relativeToDate(now, xmasDay)
              val relativeDays = (now to xmasDay).toDuration.toStandardDays.getDays
              event.getChannel.send.message(s"${KnownPlayers.steven.aliases.head} is retired from dota until ${relativeTimeMsg}")
            } else {
              sendIndividualPlayerStats(event, knownPlayer.id)
              // TODO: print out how far out of violation he is here?
            }
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
    val m = latestMatchFinder.findLatestMatch()
    val message = MatchFormatter.format(m)
    event.getChannel.send.message(message)
  }

  private def sendIndividualPlayerStats(event: MessageEvent[PircBotX], id: Long) {
    val url = s"http://dotabuff.com/players/${id}"
    val document = Jsoup.connect(url).userAgent("Mozilla").get()
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
}
