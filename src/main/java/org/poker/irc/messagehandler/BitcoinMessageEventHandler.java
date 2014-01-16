package org.poker.irc.messagehandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.MessageEventHandler;

import java.io.IOException;
import java.math.RoundingMode;

public class BitcoinMessageEventHandler implements MessageEventHandler {

  @Override
  public String[] getMessagePrefixes() {
    return new String[] { ".btc", "!btc" };
  }

  @Override
  public void onMessage(MessageEvent event) {
    // Use the factory to get the version 2 MtGox exchange API using default settings
    Exchange mtGox = ExchangeFactory.INSTANCE.createExchange("com.xeiam.xchange.mtgox.v2.MtGoxExchange");
    // Interested in the public polling market data feed (no authentication)
    PollingMarketDataService marketDataService = mtGox.getPollingMarketDataService();
    // Get the latest ticker data showing BTC to USD
    Ticker ticker;
    try {
      ticker = marketDataService.getTicker(Currencies.BTC, Currencies.USD);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    StringBuilder sb = new StringBuilder();
    sb.append("BitCoin - last: $");
    sb.append(ticker.getLast());
    sb.append(" | high: $");
    sb.append(ticker.getHigh().rounded(2, RoundingMode.UP));
    sb.append(" | low: $");
    sb.append(ticker.getLow().rounded(2, RoundingMode.UP));
    sb.append(" | vol: ");
    sb.append(ticker.getVolume());
    //sb.append(" | w.avg: ");
    //sb.append(ticker.get());
    event.getChannel().send().message(sb.toString());
    /*final String url = "https://www.mtgox.com/";
    Gson gson = new GsonBuilder().create();
    gson.
    Document document;
    try {
      document = Jsoup.connect(url).followRedirects(true).timeout(3000).get();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    // shit is broken because mtgox delay loads the data, their json API doesn't let me get anything other than
    // last price tho :(
    Element dataHolderElement = document.select("div#dataholder").first();
    Element lastPriceElement = dataHolderElement.select("li#lastPrice").first();
    Element highPriceElement = dataHolderElement.select("li#highPrice").first();
    Element lowPriceElement = dataHolderElement.select("li#lowPrice").first();
    Element weightedAveragePriceElement = dataHolderElement.select("li#weightedAverage").first();*/
  }
}
