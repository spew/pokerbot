package org.poker.irc.stock;

import org.poker.irc.HttpUtils;

public class StockTicker {
  public static Stock get(String symbol) {
    if (symbol.length() > 8) {
      return null;
    }
    Stock stock = HttpUtils.getJson("http://finance.google.com/finance/infotype=infoquoteall&q=" + symbol, Stock.class);
    return stock;
  }
}
