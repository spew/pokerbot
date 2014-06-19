package org.poker


import com.typesafe.scalalogging.slf4j.StrictLogging
import org.poker.handler._
import org.pircbotx.cap.TLSCapHandler
import org.pircbotx.hooks.Listener
import org.pircbotx.{UtilSSLSocketFactory, Configuration, PircBotX}
import org.poker.poller.{SceneAccessPoller, CoinMarketCaps}

class BotRunner(pc: ProgramConfiguration) extends StrictLogging {
  val coinMarketCaps = new CoinMarketCaps(pc)
  val ircBotConfig = this.getIrcBotConfiguration()
  val ircBot = new PircBotX(ircBotConfig)
  val sceneAccessPoller = new SceneAccessPoller(pc, ircBot)

  def run(): Unit = {
    startPollers()
    logger.debug("connecting to '{}'", pc.serverHostname)
    ircBot.startBot()
  }

  def startPollers() {
    coinMarketCaps.start()
    if (pc.sceneAccessPassword.isDefined && pc.sceneAccessUserName.isDefined) {
      sceneAccessPoller.start()
    }
  }

  def getListener(): Listener[PircBotX] = {
    val listener = new BotListener()
    listener.addHandler(new UptimeMessageEventHandler)
    listener.addHandler(new GoogleMessageEventHandler(pc))
    listener.addHandler(new UrlMessageEventHandler(pc))
    listener.addHandler(new StreamsMessageEventHandler(pc))
    listener.addHandler(new BitcoinMessageEventHandler(pc, coinMarketCaps))
    listener.addHandler(new BitcoinAddressMessageEventHandler(pc))
    listener.addHandler(new StockMessageEventHandler)
    listener.addHandler(new DotaMessageEventHandler(pc))
    listener.addHandler(new RottenTomatoesMessageEventHandler(pc))
    listener.addHandler(new SceneAccessMessageEventHandler(pc))
    listener.addHandler(new DogecoinMessageEventHandler(pc, coinMarketCaps))
    listener.addHandler(new CryptoCoinMessageEventHandler(pc, coinMarketCaps))
    listener.addHandler(new InfoMessageEventHandler)
    listener.addHandler(new WorldCupMessageEventHandler)
    if (pc.untappedClientId.isDefined && pc.untappedClientSecret.isDefined) {
      listener.addHandler(new BeerMessageEventHandler(pc.untappedClientId.get, pc.untappedClientSecret.get))
    }
    listener
  }

  def getIrcBotConfiguration(): Configuration[PircBotX] = {
    val listener = getListener()
    val builder = new Configuration.Builder()
      .setName(pc.nick)
      .setFinger(pc.finger)
      .setRealName(pc.realName)
      .setCapEnabled(true)
      .addCapHandler(new TLSCapHandler(new UtilSSLSocketFactory().trustAllCertificates(), true))
      .setAutoReconnect(true)
      .addListener(listener)
      .setLogin(pc.nick)
      .setAutoSplitMessage(true)
      .setShutdownHookEnabled(true)
      .setServerHostname(pc.serverHostname)
    for (c <- pc.channels) {
      logger.debug("adding autojoin channel: '{}'", c)
      builder.addAutoJoinChannel(c)
    }
    builder.buildConfiguration()
  }
}