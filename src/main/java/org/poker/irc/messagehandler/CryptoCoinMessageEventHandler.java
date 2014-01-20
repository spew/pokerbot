package org.poker.irc.messagehandler;

import org.apache.commons.lang3.math.*;
import org.pircbotx.hooks.events.*;
import org.poker.irc.*;

import java.math.*;
import java.lang.*;

import static org.poker.irc.cryptocoincharts.CryptoCoinChart.*;


/**
 * Created by Tom on 1/19/14.
 */
public class CryptoCoinMessageEventHandler implements MessageEventHandler{
  @Override
  public String[] getMessagePrefixes() {
    return new String[]  {"!coin" , ".coin"};
  }

  @Override
  public String getDescription() {
    return "!coin <symbol> or .coin <symbol>: gives you the latest info on a cypto coin. Include 2 symbols for a trading pair comparison.";
  }

  @Override
  public void onMessage(MessageEvent event) {
    String message = event.getMessage();
    String symbol = null;
    BigDecimal amount;
    String channelResponse = null;
    if (message.startsWith("!coin") || message.startsWith(".coin")){
      String[] commandParts = message.split(" ");
      switch(commandParts.length){
        case 1:
          // no coin specified, default to doge
          channelResponse = GetCoinInfo("doge");
          break;
        case 2:
          // display specific coin info
          symbol = commandParts[1];
          channelResponse = GetCoinInfo(symbol);
          break;
        case 3:
          // display USD value for a given amount of a given coin
          symbol = commandParts[1];
          if(NumberUtils.isNumber(commandParts[2])){
            amount = new BigDecimal(commandParts[2]);
            channelResponse = GetCoinUSDValue(symbol, amount);
          } else{
            // display comparison of 2 given coins
            String compareSymbol = commandParts[2];
            GetCoinPairInfo(symbol, compareSymbol);
          }
          break;
      }
      event.getChannel().send().message(channelResponse);
    }

  }
}
