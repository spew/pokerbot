package org.poker.irc.scc;

import com.google.common.collect.ImmutableSet;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.Period;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SceneBot {
  private static final Logger LOG = LoggerFactory.getLogger(SceneBot.class);
  private final PircBotX bot;
  private final Configuration configuration;
  private Map<SceneShow, DateTime> showToTimeMap = new ConcurrentHashMap<>();
  private Map<SceneShow, DateTime> showToLastCheckTimeMap = new HashMap<>();

  public SceneBot(final PircBotX bot, final Configuration configuration) {
    this.bot = bot;
    this.configuration = configuration;
  }

  public void start(final ScheduledExecutorService scheduler, final int delaySeconds) {
    final Credentials credentials = new Credentials(configuration.getSceneAccessUsername(), configuration.getSceneAccessPassword());
    final SceneAccess sceneAccess = new SceneAccess(credentials);
    Runnable manager = new Runnable() {
      @Override
      public void run() {
        LOG.info("Starting sceneaccess polling manager...");
        while (true) {
          final DateTime now = DateTime.now();
          for (final SceneShow show : SceneShow.values()) {
            if (showToTimeMap.containsKey(show)) {
              if (showToTimeMap.get(show).toLocalDateTime().getDayOfWeek() == now.toLocalDateTime().getDayOfWeek()) {
                continue;
              }
            }
            if (showToLastCheckTimeMap.containsKey(show)) {
              DateTime lastCheckTime = showToLastCheckTimeMap.get(show);
              int nextCheckTime = show.getWaitDurationSeconds();
              Interval interval = new Interval(lastCheckTime, now);
              if (interval.toDurationMillis() < nextCheckTime * 1000) {
                continue;
              }
            }
            showToLastCheckTimeMap.put(show, now);
            Runnable findShowRunnable = new Runnable() {
              @Override
              public void run() {
                LOG.info("Looking for show '{}'", show.getName());
                List<Torrent> torrents;
                try {
                  torrents = sceneAccess.findShow(show.getName());
                } catch (Exception e) {
                  LOG.warn("Problem obtaining show: " + show.getName(), e);
                  return;
                }
                if (!torrents.isEmpty()) {
                  Torrent torrent = torrents.get(0);
                  if (torrent.getDateAdded().getDayOfMonth() >= now.getDayOfMonth()) {
                    showToTimeMap.put(show, now);
                    for (Channel channel : bot.getUserBot().getChannels()) {
                      SceneAccessMessageEventHandler.sendTorrent(sceneAccess, channel, torrent);
                    }
                  } else {
                    LOG.info("Torrent was too old for use: url={}, dateAdded={}, now={}", sceneAccess.getUrl() + torrent.getUrl(), torrent.getDateAdded(), now);
                  }
                }
              }
            };
            scheduler.submit(findShowRunnable);
            sleep(2);
          }
          sleep(10);
        }
      }
    };
    scheduler.schedule(manager, delaySeconds, TimeUnit.SECONDS);
  }

  private int getSleepTime() {
    DateTime now = DateTime.now();
    switch (now.getDayOfWeek()) {
      case DateTimeConstants.MONDAY:
        int hour = now.toLocalDateTime().getHourOfDay();
        if (hour < 8) {
          return 60;
        } else {
          return 15 * 50;
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
