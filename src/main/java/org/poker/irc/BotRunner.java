package org.poker.irc;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.TLSCapHandler;
import org.pircbotx.exception.IrcException;
import org.poker.irc.espn.*;
import org.poker.irc.messagehandler.*;
import org.poker.irc.scc.SceneBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BotRunner {
  private Headline latestHeadline = null;
  private static final Logger LOG = LoggerFactory.getLogger(BotRunner.class);
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
  public void run(Configuration configuration) throws InterruptedException {
    org.pircbotx.Configuration ircConfiguration = this.getIrcBotConfiguration(configuration);
    final PircBotX bot = new PircBotX(ircConfiguration);
    if (configuration.isEspnEnabled()) {
      this.scheduleEspnChecker(bot, configuration);
    }
    SceneBot sceneBot = new SceneBot(bot, configuration);
    sceneBot.start(scheduler, 15);
    while (true) {
      try {
        bot.startBot();
      } catch (IOException | IrcException e) {
        LOG.warn("Unable to start the bot", e);
        Thread.sleep(1500);
      }
    }
  }

  private void scheduleEspnChecker(final PircBotX bot, final Configuration configuration) {
    Runnable checkEspnNews = new Runnable() {
      @Override
      public void run() {
        LOG.info("Checking for latest ESPN news");
        String url = "https://api.espn.com/v1/sports/news/headlines/top?apikey=" + configuration.getEspnApiKey();
        LOG.info("Fetching latest ESPN headlines");
        HeadlinesResponse headlinesResponse = HttpUtils.getJson(url, HeadlinesResponse.class, null);
        if (headlinesResponse.getHeadlines().size() == 0) {
          LOG.info("No headlines received");
          return;
        }
        Headline currentHeadline = headlinesResponse.getHeadlines().get(0);
        LOG.info("Current top headline is '{}'", currentHeadline.getHeadline());
        if (latestHeadline == null) {
          LOG.info("No previous headline, ignoring top headline");
          latestHeadline = currentHeadline;
        } else if ((latestHeadline.getId().equals(currentHeadline.getId()))) {
          LOG.info ("Previous headline matches the current headline, ignoring...");
        } else {
          LOG.info("Printing headline to channels");
          latestHeadline = currentHeadline;
          for (Channel channel : bot.getUserBot().getChannels()) {
            channel.send().message("ESPN: " + currentHeadline.getHeadline());
            channel.send().message(currentHeadline.getLinks().getWeb().getHref());
          }
        }
      }
    };
    scheduler.scheduleAtFixedRate(checkEspnNews, 0, configuration.getEspnPollIntervalMinutes(), TimeUnit.MINUTES);
  }

  private org.pircbotx.Configuration getIrcBotConfiguration(Configuration configuration) {
    EventHandler eventHandler = this.getEventHandler(configuration);
    org.pircbotx.Configuration.Builder configurationBuilder = new org.pircbotx.Configuration.Builder()
        .setName(configuration.getNick())                       // set the nick of the bot
        .setFinger("stfu pete")
        .setRealName("pete is a donk")
        .setAutoNickChange(true)                                // automatically change nick when the current one is in use
        .setCapEnabled(true)                                    // enable CAP features
        .addCapHandler(new TLSCapHandler(new UtilSSLSocketFactory().trustAllCertificates(), true))
        .addListener(eventHandler)
        .setLogin(configuration.getIdent())                     // the login is the 'ident' part of the name "login@hostmask"
        .setServerHostname(configuration.getServerHostname());
    for (String channel : configuration.getChannels()) {
      configurationBuilder.addAutoJoinChannel(channel);
    }
    return configurationBuilder.buildConfiguration();
  }

  private EventHandler getEventHandler(Configuration configuration) {
    EventHandler eventHandler = new EventHandler(configuration);
    eventHandler.addMessageEventHandler(new UrlMessageEventHandler(configuration));
    eventHandler.addMessageEventHandler(new RottenTomatoesMessageEventHandler());
    eventHandler.addMessageEventHandler(new DotabuffMessageEventHandler(configuration));
    eventHandler.addMessageEventHandler(new GoogleSearchMessageEventHandler(configuration));
    eventHandler.addMessageEventHandler(new BitcoinMessageEventHandler(configuration));
    eventHandler.addMessageEventHandler(new UptimeMessageEventHandler());
    eventHandler.addMessageEventHandler(new StreamsMessageEventHandler(configuration));
    eventHandler.addMessageEventHandler(new DogecoinMessageEventHandler(configuration));
    eventHandler.addMessageEventHandler(new CryptoCoinMessageEventHandler(configuration));
    eventHandler.addMessageEventHandler(new SceneAccessMessageEventHandler(configuration));
    eventHandler.addMessageEventHandler(new BitcoinAddressMessageEventHandler());
    eventHandler.addMessageEventHandler(new StockMessageEventHandler());
    return eventHandler;
  }
}
