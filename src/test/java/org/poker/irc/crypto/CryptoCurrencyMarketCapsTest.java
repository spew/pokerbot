package org.poker.irc.crypto;

import org.junit.Assert;
import org.junit.Test;
import org.poker.irc.Configuration;

public class CryptoCurrencyMarketCapsTest {
  @Test
  public void updateTest() {
    Configuration configuration = new Configuration();
    CryptoCurrencyMarketCaps marketCaps = new CryptoCurrencyMarketCaps(configuration);
    marketCaps.forceUpdate();
  }
}
