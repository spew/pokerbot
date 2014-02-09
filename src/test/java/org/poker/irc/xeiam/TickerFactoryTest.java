package org.poker.irc.xeiam;

import com.xeiam.xchange.dto.marketdata.Ticker;
import org.junit.Assert;
import org.junit.Test;

public class TickerFactoryTest {
  @Test
  public void getTickerTest() {
    Ticker ticker = TickerFactory.CreateBtcTicker();
    Assert.assertNotNull(ticker);
  }
}
