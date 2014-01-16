package org.poker.irc;


import org.pircbotx.hooks.types.GenericMessageEvent;

public class UrlMessageEventHandler implements MessageEventHandler {
  @Override
  public String[] getMessagePrefixes() {
    return new String[] { "http://", "https://", "www." };
  }

  @Override
  public void onMessage(GenericMessageEvent event) {
    event.respond("hello world");
    event.
    event.getBot().
  }
}
