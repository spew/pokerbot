package org.poker.yahoo

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import org.poker.util.JsonClient

class WeatherClient(consumerKey: String, consumerSecret: String) extends JsonClient {
  val baseUrl = "https://query.yahooapis.com/v1/public/yql?format=json&q="
  val headers = Nil

  def getResults(query: String) = {
    val quotedQuery = "\"" + query + "\""
    val fullQuery = s"select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=${quotedQuery})"
    val encodedQuery = URLEncoder.encode(fullQuery, StandardCharsets.UTF_8.name())
    val json = this.getJson(encodedQuery)
    json.extract[YahooResponse].query.results
  }
}


class YahooResponse(val query: YahooQueryResponse) {

}

class YahooQueryResponse (val count: Int, val lang: String, val results: YahooQueryResults) {

}

class YahooQueryResults (val channel: YahooQueryResultsChannel) {

}

class YahooQueryResultsChannel(val units: YahooQueryUnits, val item: YahooQueryItem) {

}

class YahooQueryUnits(val distance: String, val pressure: String, val speed: String, val temperature: String) {

}

class YahooQueryItem(val title: String, val condition: YahooQueryItemCondition) {

}

class YahooQueryItemCondition(val temp: String, val text: String) {

}