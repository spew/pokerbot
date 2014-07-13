package org.poker.handler

import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import org.poker.util.HumanReadable
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.PircBotX
import com.xeiam.xchange.Exchange
import com.xeiam.xchange.ExchangeFactory
import com.xeiam.xchange.service.polling.PollingMarketDataService
import com.xeiam.xchange.dto.marketdata.Ticker
import com.xeiam.xchange.currency.CurrencyPair
import java.text.NumberFormat
import org.poker.poller.CoinMarketCaps
import com.stackmob.newman.dsl._
import java.net.URL
import scala.concurrent.Await
import org.json4s.native.JsonMethods._
import com.stackmob.newman.Constants._
import org.poker.ProgramConfiguration
import org.json4s.DefaultFormats
import com.stackmob.newman.ApacheHttpClient
import scala.concurrent._
import scala.concurrent.duration._
import org.joda.money.{CurrencyUnit, BigMoney}
import org.poker.doge.DogecoinAverageResponse

class DogecoinMessageEventHandler(configuration: ProgramConfiguration, coinMarketCaps: CoinMarketCaps) extends MessageEventHandler {

  override val helpMessage: Option[String] = Option("!doge <amount>: send to channel current doge pricing information for <amount>")

  override val messageMatchRegex: Regex = "^[!.](?i)((doge)|(dogecoin)) ?(?<amount>.*)".r

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: Match): Unit = {
    val query = firstMatch.group(4)
    if (query.isEmpty) {
      val message = getMessage(1000)
      event.getChannel.send.message(message)
    } else {
      val message = getMessage(query.toInt)
      event.getChannel.send.message(message)
    }
  }

  private def getMessage(amount: Int): String = {
    val url = "http://dogecoinaverage.com/BTC.json"
    implicit lazy val formats = DefaultFormats
    implicit val httpClient = new ApacheHttpClient
    val httpRequest = GET(new URL(url))
    val httpResponse = Await.result(httpRequest.apply, 4.second)
    var body = httpResponse.bodyString(UTF8Charset)
    val numRegex = "(\"[+-]?[0-9]+[.]?[0-9]+\")".r
    body = numRegex.replaceAllIn(body, m => m.group(1).replace("\"", ""))
    val json = parse(body)
    val dogecoinResponse = json.extract[DogecoinAverageResponse]
    val volume = dogecoinResponse.markets.map(m => m.volume).sum
    val prettyVolume = (new BigDecimal(new java.math.BigDecimal(volume)) with HumanReadable).toStringHumanReadable()
    val marketCap = coinMarketCaps.getMarketCap("dogecoin")
    val formattedPrice = "%1.8f".format(dogecoinResponse.vwap)
    var message = s"DOGE/BTC: ${formattedPrice} | vol: ${prettyVolume}"
    if (marketCap.isDefined) {
      val prettyCap = (new BigDecimal(marketCap.get.bigDecimal) with HumanReadable).toStringHumanReadable()
      message += s" | cap: ${prettyCap}"
    }
    val coinbaseTicker = createTicker("com.xeiam.xchange.coinbase.CoinbaseExchange")
    val coinbaseLast = coinbaseTicker.getLast()
    val lastPrice = BigMoney.of(CurrencyUnit.USD, coinbaseLast).multipliedBy(dogecoinResponse.vwap.bigDecimal).multipliedBy(amount)
    val formatter = NumberFormat.getCurrencyInstance
    val displayedPrice = formatter.format(lastPrice.getAmount)
    message += s" | ${amount} DOGE = ${displayedPrice}"
    message
  }

  private def createTicker(className: String): Ticker = {
    val btcAverage: Exchange = ExchangeFactory.INSTANCE.createExchange(className)
    // Interested in the public polling market data feed (no authentication)
    val marketDataService: PollingMarketDataService = btcAverage.getPollingMarketDataService
    val ticker = marketDataService.getTicker(CurrencyPair.BTC_USD)
    ticker
  }


}
