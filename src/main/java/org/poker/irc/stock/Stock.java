package org.poker.irc.stock;

import com.google.gson.annotations.SerializedName;

public class Stock {
  @SerializedName("t")
  private String symbol;
  @SerializedName("l_fix")
  private double currentPriceUsd;
  @SerializedName("c_fix")
  private double currentDifferenceUsd;
  @SerializedName("cp_fix")
  private double currentDifferencePercentage;
  @SerializedName("el_fix")
  private double extraHoursPriceUsd;
  @SerializedName("ec_fix")
  private double extraHoursPriceDifferenceUsd;
  @SerializedName("ecp_fix")
  private double extraHoursCurrentPriceDifferencePercentage;

  public String getSymbol() {
    return symbol;
  }

  public double getCurrentPriceUsd() {
    return currentPriceUsd;
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
