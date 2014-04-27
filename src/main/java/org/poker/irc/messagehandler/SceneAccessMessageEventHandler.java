package org.poker.irc.messagehandler;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.Configuration;
import org.poker.irc.MessageEventHandler;
import org.poker.irc.scc.Credentials;
import org.poker.irc.scc.SceneAccess;
import org.poker.irc.scc.Torrent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SceneAccessMessageEventHandler implements MessageEventHandler {
  final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  final Configuration configuration;
  private SceneAccess sceneAccess;
  private final String[] showNames = new String[] { "game of thrones" };
  //private final Set<String> shows = Sets.immutableEnumSet(showNames);

  public SceneAccessMessageEventHandler(Configuration configuration) {
    this.configuration = configuration;
    this.sceneAccess = new SceneAccess(new Credentials(configuration.getSceneAccessUsername(),
        configuration.getSceneAccessPassword()));
  }

  @Override
  public String getMessageRegex() {
    return null;
  }

  @Override
  public String[] getMessagePrefixes() {
    return new String[] { "!scene", ".scene", "!scc", ".scc" };
  }

  @Override
  public String getDescription() {
    return "Use !scene, .scene, !scc, or .scc to get information on popular torrents";
  }

  @Override
  public void onMessage(MessageEvent event) {
    String message = event.getMessage();
    String showName = null;
    for (String s : this.getMessagePrefixes()) {
      if (message.startsWith(s)) {
        showName = message.substring(s.length()).trim();
      }
    }
    if (Strings.isNullOrEmpty(showName)) {
      event.getChannel().send().message("Please supply a show name");
      return;
    }
    List<Torrent> torrents = sceneAccess.findShow(showName);
    if (torrents.size() > 0) {
      this.sendTorrent(event.getChannel(), torrents.get(0));
    } else {
      event.getChannel().send().message("Unable to find show: " + showName);
    }
  }

  private void sendTorrent(Channel channel, Torrent torrent) {
    String url = this.sceneAccess.getUrl() + "/" + torrent.getUrl();
    String formattedDate = torrent.getDateAdded().toString("yyyy-MM-dd");
    //String formattedDate = torrent.getDateAdded().toString("yyyy-MM-dd HH:mm");
    channel.send().message(torrent.getTitle() + " | " + formattedDate + " | 720p | " + url);
  }

  public void startAutomatedChecker() {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        //sceneAccess.
      }
    };

    //this.scheduler.scheduleAtFixedRate(runnable, 0, this.configuration.getSceneAccessPollIntervalMinutes(), TimeUnit.MINUTES);
  }
}
