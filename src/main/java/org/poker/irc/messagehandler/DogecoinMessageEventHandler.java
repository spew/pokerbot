package org.poker.irc.messagehandler;

import org.apache.commons.lang3.math.*;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.MessageEventHandler;
import java.math.BigDecimal;

import static org.poker.irc.cryptocoincharts.CryptoCoinChart.*;

public class DogecoinMessageEventHandler implements MessageEventHandler {

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
