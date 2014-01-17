package org.poker.irc.messagehandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import org.joda.money.format.MoneyAmountStyle;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.MessageEventHandler;
import org.poker.irc.mtGox.TickerFactory;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.NumberFormat;

public class BitcoinMessageEventHandler implements MessageEventHandler {

  private MoneyFormatter moneyFormatter = new MoneyFormatterBuilder()
      .appendCurrencyCode()
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

    Ticker ticker = org.poker.irc.mtGox.TickerFactory.CreateBtcTicker();

    StringBuilder sb = new StringBuilder();
    sb.append("BitCoin - last: $");
    sb.append(ticker.getLast());
    sb.append(" | high: $");
    sb.append(ticker.getHigh().rounded(2, RoundingMode.UP));
    sb.append(" | low: $");
    sb.append(this.moneyFormatter.print(ticker.getLow().rounded(2, RoundingMode.UP)));
    sb.append(" | vol: ");
    sb.append(NumberFormat.getIntegerInstance().format(ticker.getVolume()));
    //sb.append(" | w.avg: ");
    //sb.append(ticker.get());
    event.getChannel().send().message(sb.toString());
  }
}
