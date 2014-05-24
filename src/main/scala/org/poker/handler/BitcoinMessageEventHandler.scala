package org.poker.handler

import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import org.poker.ProgramConfiguration
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

class BitcoinMessageEventHandler(configuration: ProgramConfiguration, coinMarketCaps: CoinMarketCaps) extends MessageEventHandler {
  val averageTicker = createTicker("com.xeiam.xchange.bitcoinaverage.BitcoinAverageExchange")
  val coinbaseTicker = createTicker("com.xeiam.xchange.coinbase.CoinbaseExchange")
  val bitstampTicker = createTicker("com.xeiam.xchange.bitstamp.BitstampExchange")

  override val helpMessage: Option[String] = Option("!btc <query>: send to channel current pricing information for <amount> bitcoin")

  override val messageMatchRegex: Regex = "[!.](?i)((btc)|(bitcoin)) ?(?<query>.*)".r

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: Match): Unit = {
    val query = Option(firstMatch.group(3)).getOrElse("").trim
    val formatter = NumberFormat.getCurrencyInstance
    val coinbaseLast = formatter.format(coinbaseTicker.getLast.doubleValue())
    val bitstampLast = formatter.format(bitstampTicker.getLast.doubleValue())
    val average = formatter.format(averageTicker.getLast.doubleValue())
    val volume = (new BigDecimal(averageTicker.getVolume) with HumanReadable).toStringHumanReadable()
    val marketCap = coinMarketCaps.getMarketCap("btc")
    var message = s"BTC - coinbase: ${coinbaseLast} | bitstamp: ${bitstampLast} | avg: ${average} | vol: ${volume}"
    if (marketCap.isDefined) {
      val cap = (new BigDecimal(marketCap.get.bigDecimal) with HumanReadable).toStringHumanReadable()
      message += s" | cap: ${cap}"
    }
    if (query.isEmpty) {
      event.getChannel.send.message(message)
    } else {
      // tODO: send btc amount to channel instead of just 1 btc
      event.getChannel.send.message(message)
    }
  }

  private def createTicker(className: String): Ticker = {
    val btcAverage: Exchange = ExchangeFactory.INSTANCE.createExchange(className)
    // Interested in the public polling market data feed (no authentication)
    val marketDataService: PollingMarketDataService = btcAverage.getPollingMarketDataService
    val ticker = marketDataService.getTicker(CurrencyPair.BTC_USD)
    ticker
  }
}
