package org.poker.irc.messagehandler;

import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.MessageEventHandler;

public class SceneAccessMessageEventHandler implements MessageEventHandler {

  @Override
  public String[] getMessagePrefixes() {
    return new String[] { "!scene", ".scene", "!scc", ".scc" };
  }

  @Override
  public void onMessage(MessageEvent event) {

  }

  @Override
  public String getDescription() {
    return "Use !scene, .scene, !scc, or .scc to get information on popular torrents";
  }
}
