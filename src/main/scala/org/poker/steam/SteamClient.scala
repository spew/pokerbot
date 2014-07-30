package org.poker.steam

import org.json4s._
import org.poker.steam.dota.{MatchDetails, Match, MatchDetailsResponse, MatchHistoryResponse}
import org.poker.util.JsonClient

class SteamClient(apiKey: String) extends JsonClient {
  val baseUrl = "https://api.steampowered.com"
  val baseDotalURl = "/IDOTA2Match_570"
  val headers = Nil

  def getLatestDotaMatches(playerId: Long, maxResults: Int, initialMatch: Option[Long] = None): List[Match] = {
    val startAtMatch = if (initialMatch.isDefined) s"&start_at_match_id=${initialMatch.get}" else ""
    val url = baseDotalURl + s"/GetMatchHistory/V001/?account_id=${playerId}&matches_requested=${maxResults}&key=${apiKey}" + startAtMatch
    val json = getJson(url)
    json.extract[MatchHistoryResponse].matches
  }

  def getDotaMatchDetails(matchId: Long) : MatchDetails = {
    val url = baseDotalURl + s"/GetMatchDetails/V001/?match_id=${matchId}&key=${apiKey}"
    val json = getJson(url)
    json.extract[MatchDetailsResponse].result
  }
}
