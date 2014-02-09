package org.poker.irc.xeiam;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

import java.io.IOException;

public class TickerFactory {
  public static Ticker CreateBtcTicker() {
    return TickerFactory.CreateBtcTicker("com.xeiam.xchange.bitcoinaverage.BitcoinAverageExchange");
  }

  public static Ticker CreateBtcTicker(String tickerClass) {
    Exchange btcAverage = ExchangeFactory.INSTANCE.createExchange(tickerClass);
    // Interested in the public polling market data feed (no authentication)
    PollingMarketDataService marketDataService = btcAverage.getPollingMarketDataService();
    // Get the latest ticker data showing BTC to USD
    Ticker ticker;
    try {
      ticker = marketDataService.getTicker(Currencies.BTC, Currencies.USD);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return ticker;
  }
}
