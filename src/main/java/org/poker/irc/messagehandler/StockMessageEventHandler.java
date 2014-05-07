package org.poker.irc.messagehandler;

import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.BotUtils;
import org.poker.irc.MessageEventHandler;
import org.poker.irc.stock.Stock;
import org.poker.irc.stock.StockTicker;

public class StockMessageEventHandler implements MessageEventHandler {
  @Override
  public String[] getMessagePrefixes() {
    return new String[] { "!stock", ".stock", "!stocks", ".stocks" };
  }

  @Override
  public String getMessageRegex() {
    return null;
  }

  @Override
  public void onMessage(MessageEvent event) {
    String message = event.getMessage();
    String symbol = null;
    for (String s : this.getMessagePrefixes()) {
      if (message.startsWith(s + " ")) {
        symbol = message.replace(s + " ", "");
      }
    }
    Stock stock = StockTicker.get(symbol);
    if (stock == null) {
      return;
    }
    String channelMessage = stock.getSymbol() + ": " + this.formatResults(stock.getCurrentPriceUsd(),
        stock.getCurrentDifferenceUsd(), stock.getCurrentDifferencePercentage());
    if (stock.getExtraHoursPriceUsd() != 0) {
      channelMessage += " | after hours: "
          + this.formatResults(stock.getExtraHoursPriceUsd(), stock.getExtraHoursPriceDifferenceUsd(),
          stock.getExtraHoursCurrentPriceDifferencePercentage());
    }
    event.getChannel().send().message(channelMessage);
  }

  private String formatResults(double price, double difference, double differencePercentage) {
    return BotUtils.formatCurrency(price) + " " + this.getDifference(difference)
        + " (" + this.getPercentage(differencePercentage) + "%)";
  }

  private String getDifference(double difference) {
    String value = BotUtils.formatCurrency(Math.abs(difference));
    if (difference >= 0) {
      value = "+" + value;
    } else {
      value = "-" + value;
    }
    return value;
  }

  private String getPercentage(double percentage) {
    String retValue = BotUtils.format2(percentage);
    if (percentage < 0) {
      return retValue;
    }
    return "+" + retValue;
  }

  @Override
  public String getDescription() {
    return "!stock or .stock on a symbol to get price updates";
  }
}
