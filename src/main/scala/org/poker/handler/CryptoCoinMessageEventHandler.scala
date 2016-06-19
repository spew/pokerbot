package org.poker.handler

import java.net.URL
import java.text.NumberFormat

import com.stackmob.newman.ApacheHttpClient
import com.stackmob.newman.Constants._
import com.stackmob.newman.dsl._
import com.xeiam.xchange.{Exchange, ExchangeFactory}
import com.xeiam.xchange.currency.CurrencyPair
import com.xeiam.xchange.dto.marketdata.Ticker
import com.xeiam.xchange.service.polling.PollingMarketDataService
import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods._
import org.poker.ProgramConfiguration
import org.poker.crypto.CryptoCoin
import org.poker.poller.CoinMarketCaps
import org.poker.util.HumanReadableLargeNumberFormatter
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.matching.Regex
import scala.util.matching.Regex.Match

class CryptoCoinMessageEventHandler(configuration: ProgramConfiguration, coinMarketCaps: CoinMarketCaps) extends MessageEventHandler {

  override val helpMessage: Option[String] = Option("!coin <symbol1> <symbol2>: send to channel current pricing information for <symbol1> compared to <symbol2>")

  override val messageMatchRegex: Regex = "^[!.](?i)((coin)|(crypto)) ?(?<amount>.*)".r

  override def onMessage(event: MessageReceivedEvent, firstMatch: Match): Unit = {
    val query = firstMatch.group(4)
    if (query.isEmpty) {
      event.getMessage.getChannel.sendMessage("usage: !coin <symbol1> <symbol2>")
    } else {
      val message = getMessage(query, None)
      event.getMessage.getChannel.sendMessage(message)
    }
  }

  private def getMessage(symbol: String, comparisonSymbol: Option[String]): String = {
    val url = "http://www.cryptocoincharts.info/v2/api/listCoins"
    implicit lazy val formats = DefaultFormats
    implicit val httpClient = new ApacheHttpClient
    val httpRequest = GET(new URL(url))
    val httpResponse = Await.result(httpRequest.apply, 2.second)
    var body = httpResponse.bodyString(UTF8Charset)
    val numRegex = "(\"[+-]?[0-9]+[.]?[0-9]*(e-)?[0-9]*\")".r
    body = numRegex.replaceAllIn(body, m => m.group(1).replace("\"", ""))
    val json = parse(body)
    val cryptoCoinChartResponse = json.extract[List[CryptoCoin]]
    val coin = cryptoCoinChartResponse.filter(c => c.id == symbol).head
    val amount = 1000
    val usdValue = this.getUsdValue(coin, amount)
    val formattedValueBtc = "%1.8f".format(coin.price_btc)
    val volume = HumanReadableLargeNumberFormatter.format((coin.volume_btc / coin.price_btc).bigDecimal)
    val marketCap = coinMarketCaps.getMarketCap(coin.name)
    var message = s"${coin.id.toUpperCase()}/BTC: ${formattedValueBtc} | vol: ${volume}"
    if (marketCap.isDefined) {
      val prettyCap = HumanReadableLargeNumberFormatter.format(marketCap.get.bigDecimal)
      message += s" | cap: ${prettyCap}"
    }
    val formatter = NumberFormat.getCurrencyInstance
    val displayedPrice = formatter.format(usdValue)
    message += s" | ${amount} ${coin.id} = ${displayedPrice}"
    message
  }

  private def getUsdValue(coin: CryptoCoin, amount: Int): BigDecimal = {
    val coinbaseTicker = createTicker("com.xeiam.xchange.coinbase.CoinbaseExchange")
    val coinbasePrice = coinbaseTicker.getLast
    val price = coinbasePrice.multiply(coin.price_btc.bigDecimal).multiply(BigDecimal(amount).bigDecimal)
    price
  }

  private def createTicker(className: String): Ticker = {
    val btcAverage: Exchange = ExchangeFactory.INSTANCE.createExchange(className)
    // Interested in the public polling market data feed (no authentication)
    val marketDataService: PollingMarketDataService = btcAverage.getPollingMarketDataService
    val ticker = marketDataService.getTicker(CurrencyPair.BTC_USD)
    ticker
  }
}
