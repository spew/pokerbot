package org.poker.poller

import java.util.concurrent.{TimeUnit, Executors, ScheduledExecutorService}
import scala.BigDecimal
import org.jsoup.nodes.{Element, Document}
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.text.{DecimalFormat, DecimalFormatSymbols}
import org.poker.ProgramConfiguration
import com.typesafe.scalalogging.slf4j.LazyLogging
import scala.collection.mutable.HashMap

class CoinMarketCaps(configuration: ProgramConfiguration) extends Poller with LazyLogging {
  val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
  private var cryptoIdToMarketCap: Map[String, BigDecimal] = Map()

  override def start(): Unit = {
    val runnable: Runnable = new Runnable {
      def run {
        try {
          update
        } catch {
          case t: Throwable => logger.warn("Unable to fetch crypto market caps", t)
        }
      }
    }
    scheduler.scheduleAtFixedRate(runnable, 0, configuration.cryptoMarketCapRefreshIntervalMinutes, TimeUnit.MINUTES)
  }

  override def stop(): Unit = {
    this.scheduler.shutdownNow
  }

  def getMarketCap(cryptoId: String): Option[BigDecimal] = {
    val cryptoIdToMarketCap: Map[String, BigDecimal] = this.getCryptoIdToMarketCap
    return cryptoIdToMarketCap.get(cryptoId.toLowerCase)
  }

  private def update {
    val document = Jsoup.connect("http://coinmarketcap.com/mineable.html").timeout(5000).get
    val elements: Elements = document.select("table#currencies").first.select("tr")
    var cryptoToMarketCap: HashMap[String, BigDecimal] = HashMap[String, BigDecimal]()
    import scala.collection.JavaConversions._
    val symbols: DecimalFormatSymbols = new DecimalFormatSymbols
    symbols.setGroupingSeparator(',')
    symbols.setDecimalSeparator('.')
    val decimalFormat: DecimalFormat = new DecimalFormat("#,##0.0#", symbols)
    decimalFormat.setParseBigDecimal(true)
    for (e <- elements.dropWhile(e => Option(e.id()).getOrElse("").isEmpty)) {
      val marketCapTd: Element = e.select("td[class=no-wrap market-cap text-right]").first
      val bigDecimal = BigDecimal(decimalFormat.parse(marketCapTd.attr("data-usd")).toString)
      cryptoToMarketCap += e.id.toLowerCase -> bigDecimal
    }
    this.setCryptoIdToMarketCap(cryptoToMarketCap.toMap)
  }

  private def setCryptoIdToMarketCap(cryptoIdToMarketCap: Map[String, BigDecimal]) {
    synchronized {
      this.cryptoIdToMarketCap = cryptoIdToMarketCap
    }
  }

  private def getCryptoIdToMarketCap: Map[String, BigDecimal] = {
    synchronized {
      cryptoIdToMarketCap
    }
  }

  def forceUpdate {
    this.update
  }
}
