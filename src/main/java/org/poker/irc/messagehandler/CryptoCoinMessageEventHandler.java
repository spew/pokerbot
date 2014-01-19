package org.poker.irc.messagehandler;

import com.google.gson.*;
import com.xeiam.xchange.dto.marketdata.*;
import org.apache.commons.lang3.math.*;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.pircbotx.hooks.events.*;
import org.poker.irc.*;

import java.io.*;
import java.math.*;
import java.text.*;
import java.util.*;
import java.lang.*;

import org.poker.irc.cryptocoincharts.*;

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
    if (message.startsWith("!coin")){
      String[] commandParts = message.split(" ");
      switch(commandParts.length){
        case 1:
          // no coin specified, default to doge
          channelResponse = DisplayCoinInfo("doge");
          break;
        case 2:
          // display specific coin info
          symbol = commandParts[1];
          channelResponse = DisplayCoinInfo(symbol);
          break;
        case 3:
          // display USD value for a given amount of a given coin
          symbol = commandParts[1];
          if(NumberUtils.isNumber(commandParts[2])){
            amount = new BigDecimal(commandParts[2]);
            channelResponse = DisplayUSDValue(symbol, amount);
          } else{
            // display comparison of 2 given coins
            String compareSymbol = commandParts[2];
            DisplayCoinPairInfo(symbol, compareSymbol);
          }
          break;
      }
      event.getChannel().send().message(channelResponse);
    }

  }

  private String DisplayCoinInfo(String symbol) {
    CryptoCoinChartResponse coin = FindCoin(symbol);
    StringBuilder sb = new StringBuilder();

    BigDecimal coinPrice = new BigDecimal(coin.getPrice_btc());

    sb.append(String.format("%s/BTC: ", symbol.toUpperCase()));
    sb.append(coin.getPrice_btc());
    sb.append(" | vol: ");
    sb.append(NumberFormat.getNumberInstance().format(Double.parseDouble(coin.getVolume_btc())));
    sb.append(String.format(" | 1000 %s = ", symbol.toUpperCase()));
    Ticker btcTicker = org.poker.irc.xeiam.TickerFactory.CreateBtcTicker();
    BotUtils.appendMoney(btcTicker.getLast().multipliedBy(coinPrice).multipliedBy(1000), sb);
    return sb.toString();
  }

  private String DisplayUSDValue(String symbol, BigDecimal amount){
    CryptoCoinChartResponse coin = FindCoin(symbol);
    StringBuilder sb = new StringBuilder();

    BigDecimal coinPrice = new BigDecimal(coin.getPrice_btc());

    sb.append(String.format("%s %s = ", amount.toString(), symbol.toUpperCase()));
    Ticker btcTicker = org.poker.irc.xeiam.TickerFactory.CreateBtcTicker();
    BotUtils.appendMoney(btcTicker.getLast().multipliedBy(coinPrice).multipliedBy(amount), sb);
    return sb.toString();
  }

  private void DisplayCoinPairInfo(String symbol, String compareSymbol){

  }

  public static CryptoCoinChartResponse FindCoin(String symbol){
    CryptoCoinChartResponse coin = null;
    CryptoCoinChartResponse foundCoin = null;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    HttpGet httpGet = new HttpGet("http://www.cryptocoincharts.info/v2/api/listCoins");
    httpGet.addHeader("Accept", "application/json");
    try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(httpGet)) {
      HttpEntity httpEntity = response.getEntity();
      try (Reader reader = new InputStreamReader(httpEntity.getContent())) {
        List<CryptoCoinChartResponse> cryptoCoinChartResponses;
        cryptoCoinChartResponses = Arrays.asList(gson.fromJson(reader, CryptoCoinChartResponse[].class));
        Iterator it = cryptoCoinChartResponses.iterator();
        while(it.hasNext()){
          coin = (CryptoCoinChartResponse)it.next();
          if(coin.getId().equals(symbol.toLowerCase())){
            foundCoin = coin;
            break;
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return foundCoin;
  }
}
