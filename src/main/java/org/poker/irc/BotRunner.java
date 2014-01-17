package org.poker.irc;

import org.pircbotx.IdentServer;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.TLSCapHandler;
import org.pircbotx.exception.IrcException;
import org.poker.irc.messagehandler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BotRunner {
  private static final Logger LOG = LoggerFactory.getLogger(BotRunner.class);
  public void run(Configuration configuration) throws InterruptedException {
    org.pircbotx.Configuration ircConfiguration = this.getIrcBotConfiguration(configuration);
    PircBotX bot = new PircBotX(ircConfiguration);
    while (true) {
      try {
        bot.startBot();
      } catch (IOException | IrcException e) {
        LOG.warn("Unable to start the bot", e);
        Thread.sleep(1500);
      }
    }
  }

  private org.pircbotx.Configuration getIrcBotConfiguration(Configuration configuration) {
    EventHandler eventHandler = this.getEventHandler(configuration);
    org.pircbotx.Configuration.Builder configurationBuilder = new org.pircbotx.Configuration.Builder()
        .setName(configuration.getNick())             // set the nick of the bot
        .setFinger("stfu pete")
        .setRealName("pete is a donk")
        .setAutoNickChange(true)        // automatically change nick when the current one is in use
        .setCapEnabled(true)            // enable CAP features
        .addCapHandler(new TLSCapHandler(new UtilSSLSocketFactory().trustAllCertificates(), true))
        .addListener(eventHandler)
        .setLogin(configuration.getIdent())
        .setServerHostname(configuration.getServerHostname());
    for (String channel : configuration.getChannels()) {
      configurationBuilder.addAutoJoinChannel(channel);
    }
    return configurationBuilder.buildConfiguration();
  }

  private EventHandler getEventHandler(Configuration configuration) {
    EventHandler eventHandler = new EventHandler();
    eventHandler.addMessageEventHandler(new UrlMessageEventHandler());
    eventHandler.addMessageEventHandler(new RottenTomatoesMessageEventHandler());
    eventHandler.addMessageEventHandler(new DotabuffMessageEventHandler());
    eventHandler.addMessageEventHandler(new GoogleSearchMessageEventHandler(configuration));
    eventHandler.addMessageEventHandler(new BitcoinMessageEventHandler());
    eventHandler.addMessageEventHandler(new UptimeMessageEventHandler());
    eventHandler.addMessageEventHandler(new StreamsMessageEventHandler(configuration));
    return eventHandler;
  }
}
