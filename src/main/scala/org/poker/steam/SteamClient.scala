package org.poker.steam

import org.json4s._
import org.json4s.native.JsonMethods._
import com.stackmob.newman.Constants._
import com.stackmob.newman.ApacheHttpClient
import java.net.URL
import com.stackmob.newman.dsl._
import scala.concurrent._
import scala.concurrent.duration._
import org.poker.steam.dota.{MatchDetails, Match, MatchDetailsResponse, MatchHistoryResponse}

class SteamClient(apiKey: String) {
  val baseUrl = "https://api.steampowered.com"
  val baseDotalURl = baseUrl + "/IDOTA2Match_570"
  implicit lazy val formats = DefaultFormats
  implicit val httpClient = new ApacheHttpClient

  def getLatestDotaMatches(playerId: Long, maxResults: Int): List[Match] = {
    val url = baseDotalURl + s"/GetMatchHistory/V001/?account_id=${playerId}&matches_requested=${maxResults}&key=${apiKey}"
    val json = getJson(url)
    json.extract[MatchHistoryResponse].matches
  }

  def getDotaMatchDetails(matchId: Long) : MatchDetails = {
    val url = baseDotalURl + s"/GetMatchDetails/V001/?match_id=${matchId}&key=${apiKey}"
    val json = getJson(url)
    val test = pretty(render(json))
    json.extract[MatchDetailsResponse].result
  }

  private def getJson(relativeUri: String): JValue = {
    val httpRequest = GET(new URL(relativeUri))
    val httpResponse = Await.result(httpRequest.apply, 2.second)
    parse(httpResponse.bodyString(UTF8Charset))
  }
}
