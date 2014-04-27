package org.poker.irc.blockchain;

import org.poker.irc.HttpUtils;

public class BlockChainInfoClient {
  String baseUrl = "https://blockchain.info/";

  public String getSingleAddressUrl(String address) {
    return baseUrl + "address/" + address;
  }

  public SingleAddress getSingleAddress(String address) {
    String url = this.getSingleAddressUrl(address) + "?format=json";
    return HttpUtils.getJson(url, SingleAddress.class);
  }
}
