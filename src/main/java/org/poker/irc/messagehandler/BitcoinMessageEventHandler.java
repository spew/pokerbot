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
    /*Ticker ticker = org.poker.irc.xeiam.TickerFactory.CreateBtcTicker();
    StringBuilder sb = new StringBuilder();
    sb.append(ticker.getTradableIdentifier().toLowerCase());
    sb.append(" - last: ");
    BotUtils.appendMoney(ticker.getLast(), sb);
    sb.append(" | ask: ");
    BotUtils.appendMoney(ticker.getAsk(), sb);
    String test = ticker.getTradableIdentifier();
    sb.append(" | vol: ");
    sb.append(NumberFormat.getIntegerInstance().format(ticker.getVolume()));  */
    StringBuilder sb = new StringBuilder();
    sb.append("BTC - ");
    BotUtils.appendMoney(BigMoney.of(CurrencyUnit.USD, getSpotRateResponse.getAmount()), sb);
    sb.append(" | https://coinbase.com/charts");
    event.getChannel().send().message(sb.toString());
  }

}
