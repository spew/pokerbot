package org.poker.irc;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.TLSCapHandler;
import org.pircbotx.exception.IrcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BotRunner {
  private static final Logger LOG = LoggerFactory.getLogger(BotRunner.class);
  public void run() throws InterruptedException {
    EventHandler eventHandler = this.getEventHandler();
    Configuration configuration = new Configuration.Builder()
        .setName("donkbot")             // set the nick of the bot
        .setAutoNickChange(true)        // automatically change nick when the current one is in use
        .setCapEnabled(true)            // enable CAP features
        .addCapHandler(new TLSCapHandler(new UtilSSLSocketFactory().trustAllCertificates(), true))
        .addListener(eventHandler)
        .setServerHostname("irc.enterthegame.com")
        .addAutoJoinChannel("#test")
        .buildConfiguration();
    PircBotX bot = new PircBotX(configuration);
    while (true) {
      try {
        bot.startBot();
      } catch (IOException | IrcException e) {
        LOG.warn("Unable to start the bot", e);
        Thread.sleep(1500);
      }
    }
  }

  private EventHandler getEventHandler() {
    EventHandler eventHandler = new EventHandler();
    eventHandler.addMessageEventHandler(new UrlMessageEventHandler());
    return eventHandler;
  }
}
