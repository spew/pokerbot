package org.poker.irc.messagehandler;

import org.apache.commons.lang3.math.NumberUtils;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.Configuration;
import org.poker.irc.MessageEventHandler;
import org.poker.irc.cryptocoincharts.CryptoCoinChart;

import java.math.BigDecimal;

import static org.poker.irc.cryptocoincharts.CryptoCoinChart.*;

public class CryptoCoinMessageEventHandler implements MessageEventHandler {
  private final CryptoCoinChart cryptoCoinChart;
  public CryptoCoinMessageEventHandler(Configuration configuration) {
    this.cryptoCoinChart = new CryptoCoinChart(configuration);
  }

  @Override
  public String getMessageRegex() {
    return null;
  }

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
          channelResponse = this.cryptoCoinChart.getCoinInfo("doge");
          break;
        case 2:
          // display specific coin info
          symbol = commandParts[1];
          channelResponse = this.cryptoCoinChart.getCoinInfo(symbol);
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
