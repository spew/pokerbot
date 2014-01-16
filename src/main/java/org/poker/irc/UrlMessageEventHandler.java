package org.poker.irc;


import org.pircbotx.hooks.events.MessageEvent;

public class UrlMessageEventHandler implements MessageEventHandler {
  @Override
  public String[] getMessagePrefixes() {
    return new String[] { "http://", "https://", "www." };
  }

  @Override
  public void onMessage(MessageEvent event) {
    event.getChannel().send().message("donk spew");
  }
}
