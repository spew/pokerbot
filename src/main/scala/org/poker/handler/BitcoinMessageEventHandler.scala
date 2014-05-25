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
  override val helpMessage: Option[String] = Option("!btc <query>: send to channel current pricing information for <amount> bitcoin")

  override val messageMatchRegex: Regex = "[!.](?i)((btc)|(bitcoin)) ?(?<query>.*)".r

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: Match): Unit = {
    val query = Option(firstMatch.group(4)).getOrElse("1000").trim
    try {
      val amount = BigDecimal(query)
      sendBtcMessage(event, amount)
    } catch {
      case n: NumberFormatException =>
        event.getChannel.send.message(s"'${query}' is not a valid numeric value")
    }
  }

  private def sendBtcMessage(event: MessageEvent[PircBotX], amount: BigDecimal): Unit = {
    val formatter = NumberFormat.getCurrencyInstance
    // have to create the tickers each time because they don't actually poll
    val averageTicker = createTicker("com.xeiam.xchange.bitcoinaverage.BitcoinAverageExchange")
    val coinbaseTicker = createTicker("com.xeiam.xchange.coinbase.CoinbaseExchange")
    val bitstampTicker = createTicker("com.xeiam.xchange.bitstamp.BitstampExchange")
    val coinbaseLast = formatter.format(coinbaseTicker.getLast)
    val bitstampLast = formatter.format(bitstampTicker.getLast)
    val average = formatter.format(averageTicker.getLast.doubleValue())
    val volume = (new BigDecimal(averageTicker.getVolume) with HumanReadable).toStringHumanReadable()
    val marketCap = coinMarketCaps.getMarketCap("btc")
    var message = s"BTC - coinbase: ${coinbaseLast} | bitstamp: ${bitstampLast} | avg: ${average} | vol: ${volume}"
    if (marketCap.isDefined) {
      val cap = (new BigDecimal(marketCap.get.bigDecimal) with HumanReadable).toStringHumanReadable()
      message += s" | cap: ${cap}"
    }
    val amountBtc = amount * coinbaseTicker.getLast
    val amountMessage = formatter.format(amountBtc)
    event.getChannel.send.message(message + s" | ${amount} BTC = ${amountMessage}")
  }

  private def createTicker(className: String): Ticker = {
    // you would think this thing would actually poll based on the name but in fact it doesn't...
    val btcAverage: Exchange = ExchangeFactory.INSTANCE.createExchange(className)
    val marketDataService = btcAverage.getPollingMarketDataService
    val ticker = marketDataService.getTicker(CurrencyPair.BTC_USD)
    ticker
  }
}
