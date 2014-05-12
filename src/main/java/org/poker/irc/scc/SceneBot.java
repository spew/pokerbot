package org.poker.irc.scc;

import com.google.common.collect.ImmutableSet;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.poker.irc.Configuration;
import org.poker.irc.messagehandler.SceneAccessMessageEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SceneBot {
  private static final Logger LOG = LoggerFactory.getLogger(SceneBot.class);
  private final PircBotX bot;
  private final Configuration configuration;
  private Set<String> shows = ImmutableSet.of("Mad Men", "Game of Thrones", "Silicon Valley", "Veep");
  private Map<String, DateTime> showToTimeMap = new HashMap<>();

  public SceneBot(final PircBotX bot, final Configuration configuration) {
    this.bot = bot;
    this.configuration = configuration;
  }

  public void start(final ScheduledExecutorService scheduler, final int delaySeconds) {
    final Runnable runnable = new Runnable() {
      @Override
      public void run() {
        Credentials credentials = new Credentials(configuration.getSceneAccessUsername(), configuration.getSceneAccessPassword());
        SceneAccess sceneAccess = new SceneAccess(credentials);
        DateTime now = DateTime.now();
        for (String showName : shows) {
          if (showToTimeMap.containsKey(showName)) {
            if (showToTimeMap.get(showName).toLocalDateTime().getDayOfWeek() == now.toLocalDateTime().getDayOfWeek()) {
              continue;
            }
          }
          LOG.info("Looking for show '{}'", showName);
          List<Torrent> torrents;
          try {
            torrents = sceneAccess.findShow(showName);
          } catch (Exception e) {
            LOG.warn("Problem obtaining show: " + showName, e);
            continue;
          }
          if (!torrents.isEmpty()) {
            Torrent torrent = torrents.get(0);
            showToTimeMap.put(showName, now);
            if (torrent.getDateAdded().getDayOfMonth() >= now.getDayOfMonth()) {
              for (Channel channel : bot.getUserBot().getChannels()) {
                SceneAccessMessageEventHandler.sendTorrent(sceneAccess, channel, torrent);
              }
            } else {
              LOG.info("Torrent was too old for use: url={}, dateAdded={}", sceneAccess.getUrl() + torrent.getUrl(), torrent.getDateAdded());
            }
            sleep(2);
          }
        }
      }
    };
    Runnable manager = new Runnable() {
      @Override
      public void run() {
        LOG.info("Starting sceneaccess polling manager...");
        while (true) {
          ScheduledFuture scheduledFuture = scheduler.schedule(runnable, 0, TimeUnit.SECONDS);
          try {
            scheduledFuture.get();
            sleep(getSleepTime());
          } catch (ExecutionException e) {
            LOG.warn("Problem running sceneaccess poller", e);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }
      }
    };
    scheduler.schedule(manager, delaySeconds, TimeUnit.SECONDS);
  }

  private int getSleepTime() {
    DateTime now = DateTime.now();
    switch (now.toLocalDateTime().getDayOfWeek()) {
      case DateTimeConstants.SUNDAY:
        int hour = now.toLocalDateTime().getHourOfDay();
        if (hour > 17) {
          return 60;
        } else {
          return 15 * 60;
        }
      default:
        return 2 * 60 * 60;
    }
  }

  public void sleep(int seconds) {
    try {
      Thread.sleep(seconds * 1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
