package org.poker.irc;

import org.pircbotx.hooks.types.GenericMessageEvent;

public interface MessageEventHandler {
  public String[] getMessagePrefixes();
  public void onMessage(final GenericMessageEvent event);
}
