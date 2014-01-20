package org.poker.irc.cryptocoincharts;

import com.google.gson.*;
import com.xeiam.xchange.dto.marketdata.*;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.poker.irc.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.io.*;
import java.math.*;
import java.text.*;
import java.util.*;

/**
 * Created by Tom on 1/19/14.
 */
public class CryptoCoinChart {
  public static String GetCoinInfo(String symbol) {
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

  public static String GetCoinUSDValue(String symbol, BigDecimal amount){
    CryptoCoinChartResponse coin = FindCoin(symbol);
    StringBuilder sb = new StringBuilder();

    BigDecimal coinPrice = new BigDecimal(coin.getPrice_btc());

    sb.append(String.format("%s %s = ", amount.toString(), symbol.toUpperCase()));
    Ticker btcTicker = org.poker.irc.xeiam.TickerFactory.CreateBtcTicker();
    BotUtils.appendMoney(btcTicker.getLast().multipliedBy(coinPrice).multipliedBy(amount), sb);
    return sb.toString();
  }

  public static String GetCoinPairInfo(String symbol, String compareSymbol){
    throw new NotImplementedException();
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
