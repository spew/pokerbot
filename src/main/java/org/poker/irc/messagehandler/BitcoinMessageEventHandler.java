package org.poker.irc.messagehandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import org.joda.money.*;
import org.joda.money.format.MoneyAmountStyle;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.MessageEventHandler;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.*;

public class BitcoinMessageEventHandler implements MessageEventHandler {

  private MoneyFormatter moneyFormatter = new MoneyFormatterBuilder()
      .appendCurrencySymbolLocalized()
      .appendAmount()
      .toFormatter();

  @Override
  public String getDescription() {
    return "!btc or .btc: gives you the latest info on BitCoin";
  }

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
    sb.append("BitCoin - last: ");
    this.appendMoney(ticker.getLast(), sb);
    sb.append(" | high: ");
    this.appendMoney(ticker.getHigh(), sb);
    sb.append(" | low: ");
    this.appendMoney(ticker.getLow(), sb);
    sb.append(" | vol: ");
    sb.append(NumberFormat.getIntegerInstance().format(ticker.getVolume()));
    //sb.append(" | w.avg: ");
    //sb.append(ticker.get());
    event.getChannel().send().message(sb.toString());
  }

  private void appendMoney(BigMoney bigMoney, StringBuilder sb) {
    sb.append(bigMoney.getCurrencyUnit().getSymbol());
    sb.append(NumberFormat.getCurrencyInstance().format(bigMoney.getAmount()));
  }
}
