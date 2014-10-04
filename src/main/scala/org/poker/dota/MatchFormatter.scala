package org.poker.dota

import java.util.concurrent.TimeUnit

import com.github.nscala_time.time.Imports._
import com.google.common.cache.{CacheLoader, LoadingCache, CacheBuilder}
import org.joda.time.DateTime
import org.jsoup.Jsoup
import org.ocpsoft.prettytime.PrettyTime
import org.poker.steam.dota.{Player, MatchDetails}

object MatchFormatter {
  val idToPlayer = KnownPlayers.all.map(kp => (kp.id, kp)).toMap

  def format(m: MatchDetails) = {
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
    message
  }

  private def fetchPlayerName(accountId: Long) = {
    val url = s"http://dotabuff.com/players/${accountId}"
    val document = Jsoup.connect(url).get()
    val nameElement = document.select("div.content-header-title h1").first
    nameElement.textNodes().get(0).text()
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
}
