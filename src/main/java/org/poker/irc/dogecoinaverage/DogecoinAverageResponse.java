  package org.poker.irc.dogecoinaverage;

  import java.util.ArrayList;
  import java.util.List;
  import javax.annotation.Generated;
  import com.google.gson.annotations.Expose;

  @Generated("com.googlecode.jsonschema2pojo")
  public class DogecoinAverageResponse {

  @Expose
  private String currency_name;
  @Expose
  private String currency_code;
  @Expose
  private List<Market> markets = new ArrayList<Market>();
  @Expose
  private String ts;
  @Expose
  private String date;
  @Expose
  private String vwap;
  @Expose
  private List<History> history = new ArrayList<History>();

  public String getCurrency_name() {
      return currency_name;
  }

  public void setCurrency_name(String currency_name) {
      this.currency_name = currency_name;
  }

  public String getCurrency_code() {
      return currency_code;
  }

  public void setCurrency_code(String currency_code) {
      this.currency_code = currency_code;
  }

  public List<Market> getMarkets() {
      return markets;
  }

  public void setMarkets(List<Market> markets) {
      this.markets = markets;
  }

  public String getTs() {
      return ts;
  }

  public void setTs(String ts) {
      this.ts = ts;
  }

  public String getDate() {
      return date;
  }

  public void setDate(String date) {
      this.date = date;
  }

  public String getVwap() {
      return vwap;
  }

  public void setVwap(String vwap) {
      this.vwap = vwap;
  }

  public List<History> getHistory() {
      return history;
  }

  public void setHistory(List<History> history) {
      this.history = history;
  }

  }