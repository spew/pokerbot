package org.poker.irc.stock;

import org.poker.irc.HttpUtils;

public class StockTicker {
  public static Stock get(String symbol) {
    if (symbol.length() > 8) {
      return null;
    }
    Stock stock = HttpUtils.getJson("http://finance.google.com/finance/info?client=ig&q=" + symbol, Stock.class);
    return stock;
    /*if (stocks.length == 0) {
      return null;
    }
    for (Stock s : stocks) {
      if (s.getSymbol().equals(symbol)) {
        return s;
      }
    }
    return stocks[0];*/
  }
}
