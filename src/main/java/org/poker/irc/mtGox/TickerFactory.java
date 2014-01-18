package org.poker.irc.mtGox;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

import java.io.IOException;

public class TickerFactory {
  public static Ticker CreateBtcTicker() {
    // Use the factory to get the version 2 MtGox exchange API using default settings
    Exchange mtGox = ExchangeFactory.INSTANCE.createExchange("com.xeiam.xchange.bitcoinaverage.BitcoinAverageExchange");
    // Interested in the public polling market data feed (no authentication)
    PollingMarketDataService marketDataService = mtGox.getPollingMarketDataService();
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
