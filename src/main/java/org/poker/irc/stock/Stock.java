package org.poker.irc.stock;

import com.google.gson.annotations.SerializedName;

public class Stock {
  @SerializedName("t")
  private String symbol;
  @SerializedName("l")
  private double curentPriceUsd;
  @SerializedName("c")
  private double currentDifferenceUsd;
  @SerializedName("cp")
  private double currentDifferencePercentage;
  @SerializedName("el")
  private double extraHoursPriceUsd;
  @SerializedName("ec")
  private double extraHoursPriceDifferenceUsd;
  @SerializedName("ecp")
  private double extraHoursCurrentPriceDifferencePercentage;

  public String getSymbol() {
    return symbol;
  }

  public double getCurentPriceUsd() {
    return curentPriceUsd;
  }

  public double getCurrentDifferenceUsd() {
    return currentDifferenceUsd;
  }

  public double getCurrentDifferencePercentage() {
    return currentDifferencePercentage;
  }

  public double getExtraHoursPriceUsd() {
    return extraHoursPriceUsd;
  }

  public double getExtraHoursPriceDifferenceUsd() {
    return extraHoursPriceDifferenceUsd;
  }

  public double getExtraHoursCurrentPriceDifferencePercentage() {
    return extraHoursCurrentPriceDifferencePercentage;
  }
}
