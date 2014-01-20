package org.poker.irc.cryptocoincharts;

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.javatuples.Pair;
import org.poker.irc.BotUtils;
import org.poker.irc.HttpUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import java.math.BigDecimal;
import java.text.NumberFormat;

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
    CryptoCoinChartResponse foundCoin = null;
    String url = "http://www.cryptocoincharts.info/v2/api/listCoins";
    CryptoCoinChartResponse[] responses = HttpUtils.getJson(url, CryptoCoinChartResponse[].class,
        new Pair<>("Accept", "application/json"));

    for(CryptoCoinChartResponse coin : responses){
      if(coin.getId().equals(symbol.toLowerCase())){
        foundCoin = coin;
        break;
      }
    }
    return foundCoin;
  }
}
