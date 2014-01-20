package org.poker.irc.messagehandler;

import org.apache.commons.lang3.math.*;
import org.joda.money.BigMoney;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.BotUtils;
import org.poker.irc.MessageEventHandler;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import static org.poker.irc.cryptocoincharts.CryptoCoinChart.*;

public class DogecoinMessageEventHandler implements MessageEventHandler {

  private MoneyFormatter moneyFormatter = new MoneyFormatterBuilder()
      .appendCurrencyCode()
      .appendAmount()
      .toFormatter();

  @Override
  public String getDescription() {
    return "!doge or .doge: send to channel latest doge financial information";
  }

  @Override
  public String[] getMessagePrefixes() {
    return new String[] { ".doge", "!doge" };
  }

  @Override
  public void onMessage(MessageEvent event) {

    String message = event.getMessage();
    String channelResponse = null;
    BigDecimal amount;

    if (message.startsWith("!doge") || message.startsWith(".doge")){
      String[] commandParts = message.split(" ");
      switch(commandParts.length){
        case 1:
          channelResponse = GetCoinInfo("doge");
          break;
        case 2:
          // display specific coin info
          if(NumberUtils.isNumber(commandParts[1])){
            amount = new BigDecimal(commandParts[1]);
            channelResponse = GetCoinUSDValue("doge", amount);
          }
          break;
      }
      event.getChannel().send().message(channelResponse);
    }
  }
}
