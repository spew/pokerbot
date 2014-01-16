package org.poker.irc.messagehandler;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.MessageEventHandler;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlMessageEventHandler implements MessageEventHandler {
  Pattern twitterPattern = Pattern.compile("https?:\\/\\/(mobile\\.)?twitter\\.com\\/.*?\\/status\\/([0-9]+)");
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
    Matcher matcher = twitterPattern.matcher(url);
    if (matcher.matches()) {
      String statusId = matcher.group(2);
      Twitter twitter = new Twitter();
      twitter.setOAuthConsumer("xPSflh20vuMYawYkW35mHw", "Rw1RlJI2xrIyzEHnwa9YQJ2ldUclUdyaeCVAcEks");

      Status status;
      try {
        status = twitter.showStatus(Long.parseLong(statusId));
      } catch (TwitterException e) {
        event.getChannel().send().message("Twitter is broken :(");
        throw new RuntimeException(e);
      }
      event.getChannel().send().message(status.getUser().getName() + ": " + status.getText());
    } else {
      String title = document.title();
      event.getChannel().send().message(title);
    }
  }
}