package org.poker.irc.blockchain;

import com.google.gson.annotations.SerializedName;

public class SingleAddress {
  private final double SATOSHI_PER_BITCOIN =  100000000.0;
  private String hash160;
  private String address;
  @SerializedName("n_tx")
  private int transactionCount;
  @SerializedName("total_received")
  private long receivedCountSatoshi;
  @SerializedName("total_sent")
  private long sentCountSatoshi;
  @SerializedName("final_balance")
  private long finalBalanceSatoshi;

  public String getHash160() {
    return hash160;
  }

  public String getAddress() {
    return address;
  }

  public int getTransactionCount() {
    return transactionCount;
  }

  public long getReceivedCountSatoshi() {
    return receivedCountSatoshi;
  }

  public long getSentCountSatoshi() {
    return sentCountSatoshi;
  }

  public long getFinalBalanceSatoshi() {
    return finalBalanceSatoshi;
  }

  public double getSentCountBtc() {
    return sentCountSatoshi / SATOSHI_PER_BITCOIN;
  }

  public double getReceivedCountBtc() {
    return receivedCountSatoshi / SATOSHI_PER_BITCOIN;
  }

  public double getFinalBalanceBtc() {
    return finalBalanceSatoshi / SATOSHI_PER_BITCOIN;
  }
}
