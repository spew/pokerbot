package org.poker.irc;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;

public class UrlMessageEventHandler implements MessageEventHandler {
  @Override
  public String[] getMessagePrefixes() {
    return new String[] { "http://", "https://", "www." };
  }

  @Override
  public void onMessage(MessageEvent event) {
    String message = event.getMessage();
    String url;
    // TODO: make this below a regex?
    int index = message.indexOf(' ');
    if (index <= 0) {
      url = message;
    } else {
      url = message.substring(0, index - 1);
    }
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
      url = "http://" + url;
    }
    Document document;
    try {
      document = Jsoup.connect(url).get();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    String title = document.title();
    event.getChannel().send().message(title);
  }
}
