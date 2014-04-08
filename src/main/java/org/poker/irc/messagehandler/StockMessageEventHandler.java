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
    event.getChannel().send().message(stock.getSymbol() + ": " + BotUtils.formatCurrency(stock.getCurentPriceUsd())
        + " (" + this.getPercentage(stock.getCurrentDifferencePercentage()) + "%) | after hours: "
        + BotUtils.formatCurrency(stock.getExtraHoursPriceUsd()) + " (" + this.getPercentage(stock.getExtraHoursCurrentPriceDifferencePercentage()) + "%)");
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
