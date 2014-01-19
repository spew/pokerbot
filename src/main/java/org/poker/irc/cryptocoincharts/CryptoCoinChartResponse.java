package org.poker.irc.cryptocoincharts;

/**
 * Created by Tom on 1/19/14.
 */

  import javax.annotation.Generated;
  import com.google.gson.annotations.Expose;

@Generated("com.googlecode.jsonschema2pojo")
public class CryptoCoinChartResponse {

  @Expose
  private String id;
  @Expose
  private String name;
  @Expose
  private String website;
  @Expose
  private String price_btc;
  @Expose
  private String volume_btc;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public String getPrice_btc() {
    return price_btc;
  }

  public void setPrice_btc(String price_btc) {
    this.price_btc = price_btc;
  }

  public String getVolume_btc() {
    return volume_btc;
  }

  public void setVolume_btc(String volume_btc) {
    this.volume_btc = volume_btc;
  }

}
