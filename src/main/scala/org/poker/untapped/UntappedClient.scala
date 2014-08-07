package org.poker.untapped

import java.nio.charset.StandardCharsets

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.poker.util.JsonClient
import java.net.{URLEncoder}

import org.scribe.builder.api.{DefaultApi20, DefaultApi10a, Api}

class UntappedClient(clientId: String, clientSecret: String, accessToken: String) extends JsonClient with LazyLogging {
  val baseUrl = s"http://api.untappd.com/v4"
  val headers = Nil

  def beerSearch(query: String, ordering: SearchOrdering = Alphabetical()) = {
    val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.name())
    val relativeUrl = getRelativeUrl("search/beer", s"q=${encodedQuery}", s"sort=${ordering.strategy}")
    val json = this.getJson(relativeUrl)
    json.extract[UntappedResponse[SearchResponse]]
  }

  def beerInfo(id: Long) = {
    val relativeUrl = getRelativeUrl(s"beer/info/${id}")
    val json = this.getJson(relativeUrl)
    json.extract[UntappedResponse[BeerInfoResult]]
  }

  def recentFriendCheckins() = {
    val relativeUrl = getRelativeUrl("checkin/recent")
    val json = this.getJson(relativeUrl)
    json.extract[UntappedResponse[CheckinsResponse]]
  }

  def recentCheckins(userName: String) = {
    val relativeUrl = getRelativeUrl(s"user/checkins/${userName}", "limit=5")
    val json = this.getJson(relativeUrl)
    json.extract[UntappedResponse[CheckinsResponse]]
  }

  private def getRelativeUrl(methodName: String, args: String*) = {
    val authorizationArgs = Seq(s"client_id=${clientId}", s"client_secret=${clientSecret}", s"access_token=${accessToken}")
    val allArgs = authorizationArgs++:args
    s"/${methodName}?" + allArgs.map(a => "&" + a).mkString
  }
}

abstract sealed class SearchOrdering(val strategy: String)
case class Alphabetical extends SearchOrdering("name")
case class CheckinCount extends SearchOrdering("count")