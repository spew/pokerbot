package org.poker.irc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.pircbotx.Channel;
import org.pircbotx.IdentServer;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.TLSCapHandler;
import org.pircbotx.exception.IrcException;
import org.poker.irc.espn.*;
import org.poker.irc.messagehandler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BotRunner {
  private String latestHeadlineTitle = null;
  private static final Logger LOG = LoggerFactory.getLogger(BotRunner.class);
  public void run(Configuration configuration) throws InterruptedException {
    org.pircbotx.Configuration ircConfiguration = this.getIrcBotConfiguration(configuration);
    final PircBotX bot = new PircBotX(ircConfiguration);
    this.scheduleEspnChecker(bot, configuration);
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
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
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
        String currentHeadlineTitle = currentHeadline.getHeadline();
        if (latestHeadlineTitle == null) {
          LOG.info("No previous headline, ignoring top headline");
          latestHeadlineTitle = currentHeadlineTitle;
        } else if ((latestHeadlineTitle.equalsIgnoreCase(currentHeadlineTitle))) {
          LOG.info ("Previous headline matches the current headline, ignoring...");
        } else {
          LOG.info("Printing headline to channels");
          latestHeadlineTitle = currentHeadlineTitle;
          for (Channel channel : bot.getUserBot().getChannels()) {
            channel.send().message("ESPN: " + latestHeadlineTitle);
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
    EventHandler eventHandler = new EventHandler();
    eventHandler.addMessageEventHandler(new UrlMessageEventHandler(configuration));
    eventHandler.addMessageEventHandler(new RottenTomatoesMessageEventHandler());
    eventHandler.addMessageEventHandler(new DotabuffMessageEventHandler(configuration));
    eventHandler.addMessageEventHandler(new GoogleSearchMessageEventHandler(configuration));
    eventHandler.addMessageEventHandler(new BitcoinMessageEventHandler());
    eventHandler.addMessageEventHandler(new UptimeMessageEventHandler());
    eventHandler.addMessageEventHandler(new StreamsMessageEventHandler(configuration));
    eventHandler.addMessageEventHandler(new DogecoinMessageEventHandler());
    return eventHandler;
  }
}
