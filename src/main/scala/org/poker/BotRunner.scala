package org.poker


import com.typesafe.scalalogging.slf4j.StrictLogging
import org.poker.handler._
import org.poker.poller._
import sx.blah.discord.api.{ClientBuilder, IDiscordClient}


class BotRunner(pc: ProgramConfiguration) extends StrictLogging {
  val coinMarketCaps = new CoinMarketCaps(pc)
  val discordBot = buildDiscordBot();
  val messageSender = new DiscordMessageSender
  lazy val sceneAccessPoller = new SceneAccessPoller(pc, messageSender)
  lazy val untappdPoller = new UntappdPoller(pc, messageSender)
  lazy val dotaPoller = new DotaPoller(pc, messageSender)

  def run(): Unit = {
    discordBot.getDispatcher.registerListener(getListener())
    discordBot.login()
    startPollers()
  }

  def startPollers() {
    coinMarketCaps.start()
    if (pc.sceneAccessPassword.isDefined && pc.sceneAccessUserName.isDefined) {
      sceneAccessPoller.start()
    }
    if (untappdEnabled && !pc.testMode) {
      untappdPoller.start()
    }
    if (dotaEnabled) {
      dotaPoller.start()
    }
  }

  lazy val dotaEnabled =  {
    pc.steamApiKey.isDefined
  }

  lazy val untappdEnabled = {
    pc.untappdClientId.isDefined && pc.untappdClientSecret.isDefined && pc.untappdAccessToken.isDefined
  }

  lazy val weatherEnabled = {
    pc.yahooConsumerKey.isDefined && pc.yahooConsumerSecret.isDefined
  }

  def getListener(): DiscordListener = {
    val listener = new DiscordListener()
    listener.addHandler(new UptimeMessageEventHandler)
    listener.addHandler(new GoogleMessageEventHandler(pc))
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
    listener.addHandler(new ImdbMessageEventHandler)
    if (untappdEnabled) {
      listener.addHandler(new BeerMessageEventHandler(pc.untappdClientId.get, pc.untappdClientSecret.get, pc.untappdAccessToken.get))
    }
    if (weatherEnabled) {
      listener.addHandler(new WeatherMessageHandler(pc))
    }
    listener
  }

  def buildDiscordBot(): IDiscordClient = {
    new ClientBuilder()
      .withToken(pc.discordToken.get)
      .setDaemon(true)
      .withReconnects()
      .build()
  }
}