package org.poker.irc.messagehandler;

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.BotUtils;
import org.poker.irc.HttpUtils;
import org.poker.irc.MessageEventHandler;
import org.poker.irc.coinbase.GetSpotRateResponse;

import java.text.*;

public class BitcoinMessageEventHandler implements MessageEventHandler {
  @Override
  public String getDescription() {
    return "!btc, .btc, !bitcoin, or .bitcoin: send to channel the latest bitcoin financial information from https://bitcoinaverage.com/";
  }

  @Override
  public String[] getMessagePrefixes() {
    return new String[] { ".btc", "!btc", ".bicoin", "!bitcoin" };
  }

  @Override
  public void onMessage(MessageEvent event) {
    GetSpotRateResponse getSpotRateResponse = HttpUtils.getJson("https://coinbase.com/api/v1/prices/spot_rate", GetSpotRateResponse.class);
    Ticker ticker = org.poker.irc.xeiam.TickerFactory.CreateBtcTicker();
    Ticker bitstampTicker = org.poker.irc.xeiam.TickerFactory.CreateBtcTicker("com.xeiam.xchange.bitstamp.BitstampExchange");
    StringBuilder sb = new StringBuilder();
    sb.append("BTC - coinbase: ");
    BotUtils.appendMoney(BigMoney.of(CurrencyUnit.USD, getSpotRateResponse.getAmount()), sb);
    sb.append(" | bitstamp: ");
    BotUtils.appendMoney(BigMoney.of(CurrencyUnit.USD, bitstampTicker.getLast().getAmount()), sb);
    sb.append(" | avg: ");
    BotUtils.appendMoney(BigMoney.of(CurrencyUnit.USD, ticker.getLast().getAmount()), sb);
    sb.append(" | vol: ");
    sb.append(BotUtils.format(ticker.getVolume().doubleValue()));
    event.getChannel().send().message(sb.toString());
  }

}
