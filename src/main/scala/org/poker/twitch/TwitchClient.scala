package org.poker.twitch

import org.json4s._
import com.stackmob.newman.ApacheHttpClient
import java.net.{URLEncoder, URL}
import com.stackmob.newman.dsl._
import org.json4s.native.JsonMethods._
import scala.concurrent._
import scala.concurrent.duration._
import com.stackmob.newman.request.HttpRequest
import com.stackmob.newman.Constants._
import java.nio.charset.StandardCharsets

class TwitchClient(val clientId: String) {
  implicit lazy val formats = DefaultFormats
  implicit val httpClient = new ApacheHttpClient
  val baseUrl = "https://api.twitch.tv/kraken"
  val headers = ("Accept", "application/vnd.twitchtv.v2+json")::("Client-ID", clientId)::Nil

  def queryStreams(gameName: String, resultLimit: Int): StreamsResponse = {
    val urlEncodedGameName = URLEncoder.encode(gameName, StandardCharsets.UTF_8.name())
    val json = getJson(s"/streams?limit=${resultLimit}&game=${urlEncodedGameName}")
    json.extract[StreamsResponse]
  }

  def queryGames(gameNameQuery: String): SearchGamesResponse = {
    val urlEncodedGameName = URLEncoder.encode(gameNameQuery, StandardCharsets.UTF_8.name())
    val json = getJson(s"/search/games?q=${urlEncodedGameName}&type=suggest&live=true")
    json.extract[SearchGamesResponse]
  }

  private def getJson(relativeUri: String): JValue = {
    val httpRequest = getRequest(relativeUri)
    val httpResponse = Await.result(httpRequest.apply, 2.second)
    parse(httpResponse.bodyString(UTF8Charset))
  }

  private def getRequest(relativeUrl: String): HttpRequest = {
    val url = new URL(baseUrl + relativeUrl)
    GET(url).addHeaders(headers)
  }
}
