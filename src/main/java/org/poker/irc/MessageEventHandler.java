package org.poker.irc;

import org.pircbotx.hooks.events.MessageEvent;

public interface MessageEventHandler {
  public String getMessageRegex();
  public String[] getMessagePrefixes();
  public void onMessage(final MessageEvent event);
  public String getDescription();
}
