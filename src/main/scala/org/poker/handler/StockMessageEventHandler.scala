package org.poker.handler

import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.PircBotX
import com.github.nscala_time.time.Imports._
import org.joda.time.format.{PeriodFormatterBuilder, PeriodFormatter}
import org.poker.stock.StockTicker
import java.text.NumberFormat

class StockMessageEventHandler extends MessageEventHandler {
  val startTime = DateTime.now
  val stockTicker = new StockTicker
  val currencyFormatter = NumberFormat.getCurrencyInstance

  override val helpMessage: Option[String] = Option("!stock <symbol>: send current pricing information for symbol to channel")

  override val messageMatchRegex: Regex = "^[!.](?i)((stock)|(quote)) ?(?<symbol>.*)".r

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: Match): Unit = {
    val symbol = firstMatch.group(4)
    if (symbol.isEmpty) {
      event.getChannel.send().message("usage: !stock <symbol>")
    } else {
      if (symbol.size < 8) {
        val message = this.getMessage(symbol)
        event.getChannel.send().message(message)
      }
    }
  }

  def getMessage(symbol: String): String = {
    val quote = stockTicker.get(symbol)
    val currentPriceMessage = this.formatResults(quote.currentPriceUsd, quote.currentPriceDifferenceUsd, quote.currentDifferencePercentage)
    val message = s"${quote.symbol}: ${currentPriceMessage}"
    if (quote.extraHoursPriceUsd.isDefined) {
      message + " | after hours: " + this.formatResults(
        quote.extraHoursPriceUsd.get,
        quote.extraHoursPriceDifferenceUsd.get,
        quote.extraHoursCurrentPriceDifferencePercentage.get)
    } else {
      message
    }
  }

  def formatResults(price: Double, difference: Double, differencePercentage: Double): String = {
    s"${currencyFormatter.format(price)} ${formatDifference(difference)} (${formatDifferencePercentage(differencePercentage)}%)"
  }
  
  def formatDifference(difference: Double): String = {
    val value = currencyFormatter.format(Math.abs(difference))
    if (difference >= 0) {
      "+" + value
    } else {
      "-" + value
    }
  }
  
  def formatDifferencePercentage(differencePercentage: Double): String = {
    val formatted = "%1.2f".format(differencePercentage)
    if (differencePercentage < 0) {
      formatted
    } else {
      "+" + formatted
    }
  }
}
