package org.poker.handler

import java.text.NumberFormat

import com.xeiam.xchange.{Exchange, ExchangeFactory}
import com.xeiam.xchange.currency.CurrencyPair
import com.xeiam.xchange.dto.marketdata.Ticker
import org.poker.ProgramConfiguration
import org.poker.poller.CoinMarketCaps
import org.poker.util.HumanReadableLargeNumberFormatter
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

class BitcoinMessageEventHandler(configuration: ProgramConfiguration, coinMarketCaps: CoinMarketCaps) extends MessageEventHandler {
  override val helpMessage: Option[String] = Option("!btc <query>: send to channel current pricing information for <amount> bitcoin")

  override val messageMatchRegex: Regex = "^[!.](?i)((btc)|(bitcoin)) ?(?<query>.*)".r

  override def onMessage(event: MessageReceivedEvent, firstMatch: Match): Unit = {
    val query = Option(firstMatch.group(4)).getOrElse("1000").trim
    try {
      val amount = if (query.isEmpty) None else Option(BigDecimal(query))
      sendBtcMessage(event, amount)
    } catch {
      case n: NumberFormatException =>
        event.getMessage.getChannel.sendMessage(s"'${query}' is not a valid numeric value")
    }
  }

  private def sendBtcMessage(event: MessageReceivedEvent, amount: Option[BigDecimal]): Unit = {
    val formatter = NumberFormat.getCurrencyInstance
    // have to create the tickers each time because they don't actually poll
    val averageTicker = createTicker("com.xeiam.xchange.bitcoinaverage.BitcoinAverageExchange")
    val coinbaseLast = getLast("com.xeiam.xchange.coinbase.CoinbaseExchange")
    val bitstampLast = getLast("com.xeiam.xchange.bitstamp.BitstampExchange")
    val averageLast = formatter.format(averageTicker.getLast.doubleValue())
    val average = formatter.format(averageTicker.getLast.doubleValue())
    val volume = HumanReadableLargeNumberFormatter.format(averageTicker.getVolume)
    val marketCap = coinMarketCaps.getMarketCap("bitcoin")
    var message = s"BTC - coinbase: ${coinbaseLast} | bitstamp: ${bitstampLast} | avg: ${average} | vol: ${volume}"
    if (marketCap.isDefined) {
      val cap = HumanReadableLargeNumberFormatter.format(marketCap.get.bigDecimal)
      message += s" | cap: ${cap}"
    }
    if (amount.isDefined) {
      val amountBtc = amount.get * averageTicker.getLast
      val amountMessage = formatter.format(amountBtc)
      message += s" | ${amount.get} BTC = ${amountMessage}"
    }
    event.getMessage.getChannel.sendMessage(message)
  }

  private def getLast(className: String): String = {
    val formatter = NumberFormat.getCurrencyInstance
    try {
      val ticker = createTicker(className)
      formatter.format(ticker.getLast)
    } catch {
      case _: Throwable => "n/a"
    }
  }

  private def createTicker(className: String): Ticker = {
    // you would think this thing would actually poll based on the name but in fact it doesn't...
    val btcAverage: Exchange = ExchangeFactory.INSTANCE.createExchange(className)
    val marketDataService = btcAverage.getPollingMarketDataService
    val ticker = marketDataService.getTicker(CurrencyPair.BTC_USD)
    ticker
  }
}
