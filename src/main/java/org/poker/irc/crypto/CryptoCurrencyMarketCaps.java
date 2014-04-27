package org.poker.irc.crypto;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.Maps;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.poker.irc.Configuration;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CryptoCurrencyMarketCaps implements AutoCloseable {
  final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private Map<String, BigDecimal> cryptoIdToMarketCap = Maps.newHashMap();

  public CryptoCurrencyMarketCaps(Configuration configuration) {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        CryptoCurrencyMarketCaps.this.update();
      }
    };
    scheduler.scheduleAtFixedRate(runnable, 0, configuration.getCryptoMarketCapRefreshPeriod(), TimeUnit.MINUTES);
  }

  @Override
  public void close() throws Exception {
    this.scheduler.shutdownNow();
  }

  public BigDecimal getMarketCap(String cryptoId) {
    Map<String, BigDecimal> cryptoIdToMarketCap = this.getCryptoIdToMarketCap();
    return cryptoIdToMarketCap.get(cryptoId.toLowerCase());
  }

  private void update()  {
    Document document;
    try {
      document = Jsoup.connect("http://coinmarketcap.com/mineable.html").get();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Elements elements = document.select("table#currencies").first().select("tr");
    Map<String, BigDecimal> cryptoToMarketCap = Maps.newHashMap();
    for (final Element e : elements) {
      if (Strings.isNullOrEmpty(e.id())) {
        continue;
      }
      Element marketCapTd = e.select("td[class=no-wrap market-cap text-right]").first();
      DecimalFormatSymbols symbols = new DecimalFormatSymbols();
      symbols.setGroupingSeparator(',');
      symbols.setDecimalSeparator('.');
      String pattern = "#,##0.0#";
      DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
      decimalFormat.setParseBigDecimal(true);
      // parse the string
      final BigDecimal bigDecimal;
      try {
        bigDecimal = (BigDecimal) decimalFormat.parse(marketCapTd.attr("data-usd"));
      } catch (ParseException ex) {
        throw new RuntimeException(ex);
      }
      cryptoToMarketCap.put(e.id().toLowerCase(), bigDecimal);
      CoinInfo coinInfo = new CoinInfo() {
        {
          marketCap = bigDecimal;
          symbol = e.id().toLowerCase();
        }
      };
    }
    this.setCryptoIdToMarketCap(cryptoToMarketCap);
  }

  private synchronized void setCryptoIdToMarketCap(Map<String, BigDecimal> cryptoIdToMarketCap) {
    this.cryptoIdToMarketCap = cryptoIdToMarketCap;
  }

  private synchronized Map<String, BigDecimal> getCryptoIdToMarketCap() {
    return this.cryptoIdToMarketCap;
  }

  public void forceUpdate() {
    this.update();
  }
}
