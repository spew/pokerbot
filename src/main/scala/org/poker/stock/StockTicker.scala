package org.poker.stock

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.json4s.DefaultFormats
import com.stackmob.newman.ApacheHttpClient
import com.stackmob.newman.dsl._
import java.nio.charset.StandardCharsets
import java.net.{URLEncoder, URL}
import org.json4s.native.JsonMethods._
import com.stackmob.newman.Constants._
import scala.concurrent._
import scala.concurrent.duration._

class StockTicker extends LazyLogging {
  val baseUrl = "http://finance.google.com/finance/info?client=ig&q="
  implicit lazy val formats = DefaultFormats
  implicit val httpClient = new ApacheHttpClient

  def get(symbol: String): StockQuote = {
    val urlEncodedSymbol = URLEncoder.encode(symbol, StandardCharsets.UTF_8.name())
    val httpRequest = GET(new URL(baseUrl + urlEncodedSymbol)).addHeaders(("User-Agent", "Mozilla"))
    val httpResponse = Await.result(httpRequest.apply, 2.second)
    var body = httpResponse.bodyString(UTF8Charset).replace("//", "").replace("[", "").replace("]", "").replace("\"\n,", "\",\n").replace(" : ", ":").trim
    val numRegex = "(\"[+-]?[0-9]+[.][0-9]+\")".r
    body = numRegex.replaceAllIn(body, m => m.group(1).replace("\"", ""))
    val json = parse(body)
    json.extract[StockQuote]
  }
}
