package org.poker.untapped

import org.poker.util.JsonClient
import java.net.URLEncoder

class UntappedClient(clientId: String, clientSecret: String) extends JsonClient {
  val baseUrl = s"http://api.untappd.com/v4"
  val headers = Nil

  def beerSearch(query: String, ordering: SearchOrdering = Alphabetical()) = {
    val encodedQuery = URLEncoder.encode(query)
    val relativeUrl = getRelativeUrl("search/beer", s"q=${encodedQuery}", s"sort=${ordering.strategy}")
    val json = this.getJson(relativeUrl)
    json.extract[UntappedResponse[SearchResponse]]
  }

  def beerInfo(id: Long) = {
    val relativeUrl = getRelativeUrl(s"beer/info/${id}")
    val json = this.getJson(relativeUrl)
    json.extract[UntappedResponse[BeerInfoResult]]
  }

  private def getRelativeUrl(methodName: String, args: String*) = {
    "/" + methodName + s"?client_id=${clientId}&client_secret=${clientSecret}" + args.map(a => "&" + a).mkString
  }
}


abstract sealed class SearchOrdering(val strategy: String)
case class Alphabetical extends SearchOrdering("name")
case class CheckinCount extends SearchOrdering("count")