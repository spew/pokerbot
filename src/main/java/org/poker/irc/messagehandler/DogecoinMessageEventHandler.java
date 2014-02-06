package org.poker.irc.messagehandler;

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.apache.commons.lang3.math.*;
import org.javatuples.Pair;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.BotUtils;
import org.poker.irc.HttpUtils;
import org.poker.irc.MessageEventHandler;
import org.poker.irc.coinbase.GetSpotRateResponse;
import org.poker.irc.dogecoinaverage.DogecoinAverageResponse;
import org.poker.irc.dogecoinaverage.Market;

import java.math.BigDecimal;
import java.text.NumberFormat;

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
        channelResponse = GetDogeInfo();
        break;
      case 2:
        if(NumberUtils.isNumber(commandParts[1])){
          amount = new BigDecimal(commandParts[1]);
          channelResponse = GetDogeUSDValue(amount);
        }
        break;
    }

    event.getChannel().send().message(channelResponse);
    }
  }

  String apiUrl = "http://dogecoinaverage.com/BTC.json";

  public String GetDogeInfo(){
    DogecoinAverageResponse response = HttpUtils.getJson(apiUrl, DogecoinAverageResponse.class,
      new Pair<>("Accept", "application/json"));
    StringBuilder sb = new StringBuilder();

    BigDecimal dogePrice = new BigDecimal(response.getVwap());
    Long volume = 0L;

    for(Market market : response.getMarkets()){
      volume += Long.parseLong(market.getVolume());
    }

    BigDecimal dogeVolume = new BigDecimal(volume);
    String prettyVolume = BotUtils.format(dogeVolume.doubleValue());
    BigDecimal btcVolume = dogePrice.multiply(dogeVolume);

    sb.append("DOGE/BTC: ");
    sb.append(response.getVwap());
    sb.append(" | vol: ");
    sb.append(prettyVolume);
    sb.append(" | 1000 DOGE = ");
    //Ticker btcTicker = org.poker.irc.xeiam.TickerFactory.CreateBtcTicker();
    GetSpotRateResponse getSpotRateResponse = HttpUtils.getJson("https://coinbase.com/api/v1/prices/spot_rate",
        GetSpotRateResponse.class);
    BigMoney last = BigMoney.of(CurrencyUnit.USD, getSpotRateResponse.getAmount());
    BotUtils.appendMoney(last.multipliedBy(dogePrice).multipliedBy(1000), sb);
    return sb.toString();
  }

  public String GetDogeUSDValue(BigDecimal amount){
    DogecoinAverageResponse response = HttpUtils.getJson(apiUrl, DogecoinAverageResponse.class,
        new Pair<>("Accept", "application/json"));

    StringBuilder sb = new StringBuilder();
    BigDecimal coinPrice = new BigDecimal(response.getVwap());

    sb.append(String.format("%s DOGE = ", amount.toString()));
    Ticker btcTicker = org.poker.irc.xeiam.TickerFactory.CreateBtcTicker();
    BotUtils.appendMoney(btcTicker.getLast().multipliedBy(coinPrice).multipliedBy(amount), sb);
    return sb.toString();
  }
}
