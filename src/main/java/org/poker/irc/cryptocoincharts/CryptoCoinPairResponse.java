package org.poker.irc.cryptocoincharts;

/**
 * Created by Tom on 1/19/14.
 */

  import javax.annotation.Generated;
  import com.google.gson.annotations.Expose;

@Generated("com.googlecode.jsonschema2pojo")
public class CryptoCoinPairResponse {

  @Expose
  private String id;
  @Expose
  private String price;
  @Expose
  private String price_before_24h;
  @Expose
  private String volume_first;
  @Expose
  private String volume_second;
  @Expose
  private String volume_btc;
  @Expose
  private String best_market;
  @Expose
  private String latest_trade;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public String getPrice_before_24h() {
    return price_before_24h;
  }

  public void setPrice_before_24h(String price_before_24h) {
    this.price_before_24h = price_before_24h;
  }

  public String getVolume_first() {
    return volume_first;
  }

  public void setVolume_first(String volume_first) {
    this.volume_first = volume_first;
  }

  public String getVolume_second() {
    return volume_second;
  }

  public void setVolume_second(String volume_second) {
    this.volume_second = volume_second;
  }

  public String getVolume_btc() {
    return volume_btc;
  }

  public void setVolume_btc(String volume_btc) {
    this.volume_btc = volume_btc;
  }

  public String getBest_market() {
    return best_market;
  }

  public void setBest_market(String best_market) {
    this.best_market = best_market;
  }

  public String getLatest_trade() {
    return latest_trade;
  }

  public void setLatest_trade(String latest_trade) {
    this.latest_trade = latest_trade;
  }

}
