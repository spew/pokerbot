package org.poker.irc.dogecoinaverage;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("com.googlecode.jsonschema2pojo")
public class Market {

  @Expose
  private String price;
  @Expose
  private String volume;
  @Expose
  private String ts;
  @Expose
  private String market_url;
  @Expose
  private String exchange_name;

  public String getPrice() {
      return price;
  }

  public void setPrice(String price) {
      this.price = price;
  }

  public String getVolume() {
      return volume;
  }

  public void setVolume(String volume) {
      this.volume = volume;
  }

  public String getTs() {
      return ts;
  }

  public void setTs(String ts) {
      this.ts = ts;
  }

  public String getMarket_url() {
      return market_url;
  }

  public void setMarket_url(String market_url) {
      this.market_url = market_url;
  }

  public String getExchange_name() {
      return exchange_name;
  }

  public void setExchange_name(String exchange_name) {
      this.exchange_name = exchange_name;
  }

}