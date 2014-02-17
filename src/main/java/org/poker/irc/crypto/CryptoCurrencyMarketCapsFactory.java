package org.poker.irc.crypto;

import org.poker.irc.Configuration;

public class CryptoCurrencyMarketCapsFactory {
  private CryptoCurrencyMarketCaps marketCaps;

  public CryptoCurrencyMarketCaps createCryptoCurrencyMarketCaps(Configuration configuration) {
    if (this.marketCaps == null) {
      this.marketCaps = new CryptoCurrencyMarketCaps(configuration);
    }
    return this.marketCaps;
  }
}
