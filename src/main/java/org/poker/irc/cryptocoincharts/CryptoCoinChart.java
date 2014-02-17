package org.poker.irc.cryptocoincharts;

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.javatuples.Pair;
import org.poker.irc.BotUtils;
import org.poker.irc.Configuration;
import org.poker.irc.HttpUtils;
import org.poker.irc.crypto.CryptoCurrencyMarketCaps;
import org.poker.irc.crypto.CryptoCurrencyMarketCapsFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.NumberFormat;

/**
 * Created by Tom on 1/19/14.
 */
public class CryptoCoinChart {
  private final CryptoCurrencyMarketCaps marketCaps;
  private final Ticker btcTicker;
  public CryptoCoinChart(Configuration configuration) {
    this.marketCaps = new CryptoCurrencyMarketCapsFactory().createCryptoCurrencyMarketCaps(configuration);
    this.btcTicker = org.poker.irc.xeiam.TickerFactory.CreateBtcTicker();
  }

  public String getCoinInfo(String symbol) {
    CryptoCoinChartResponse coin = FindCoin(symbol);
    StringBuilder sb = new StringBuilder();
    BigDecimal coinPrice = new BigDecimal(coin.getPrice_btc());
    sb.append(String.format("%s/BTC: ", symbol.toUpperCase()));
    sb.append(coin.getPrice_btc());
    sb.append(" | vol: ");
    BigDecimal btcVolume = new BigDecimal(Double.parseDouble(coin.getVolume_btc()));
    BigDecimal btcPrice = new BigDecimal(Double.parseDouble(coin.getPrice_btc()));
    BigDecimal volume = btcVolume.divide(btcPrice, MathContext.DECIMAL128);
    sb.append(BotUtils.format(volume.doubleValue()));
    sb.append(" | cap: ");
    sb.append(BotUtils.format(this.marketCaps.getMarketCap(symbol).doubleValue()));
    sb.append(String.format(" | 1000 %s = ", symbol.toUpperCase()));
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
