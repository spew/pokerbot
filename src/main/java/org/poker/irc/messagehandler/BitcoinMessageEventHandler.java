package org.poker.irc.messagehandler;

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.BotUtils;
import org.poker.irc.MessageEventHandler;

import java.text.*;

public class BitcoinMessageEventHandler implements MessageEventHandler {
  @Override
  public String getDescription() {
    return "!btc, !bitcoin, .btc, or .bitcoin: gives you the latest info on BitCoin";
  }

  @Override
  public String[] getMessagePrefixes() {
    return new String[] { ".btc", "!btc", ".bicoin", "!bitcoin" };
  }

  @Override
  public void onMessage(MessageEvent event) {
    Ticker ticker = org.poker.irc.xeiam.TickerFactory.CreateBtcTicker();
    StringBuilder sb = new StringBuilder();
    sb.append(ticker.getTradableIdentifier());
    sb.append(" - last: ");
    BotUtils.appendMoney(ticker.getLast(), sb);
    sb.append(" | ask: ");
    BotUtils.appendMoney(ticker.getAsk(), sb);
    String test = ticker.getTradableIdentifier();
    sb.append(" | vol: ");
    sb.append(NumberFormat.getIntegerInstance().format(ticker.getVolume()));
    event.getChannel().send().message(sb.toString());
  }

}
