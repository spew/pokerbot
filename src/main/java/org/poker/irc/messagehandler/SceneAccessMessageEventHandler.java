package org.poker.irc.messagehandler;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.Configuration;
import org.poker.irc.MessageEventHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SceneAccessMessageEventHandler implements MessageEventHandler {
  final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  final Configuration configuration;

  public SceneAccessMessageEventHandler(Configuration configuration) {
    this.configuration = configuration;
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
    event.getChannel().send().message("Unable to find show: " + showName);
  }

  public void startAutomatedChecker() {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {

      }
    };

    this.scheduler.scheduleAtFixedRate(runnable, 0, this.configuration.getEspnPollIntervalMinutes(), TimeUnit.MINUTES);
  }
}
