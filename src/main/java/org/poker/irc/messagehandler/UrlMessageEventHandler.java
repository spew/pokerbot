package org.poker.irc.messagehandler;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.*;
import org.poker.irc.Configuration;
import twitter4j.*;
import twitter4j.conf.*;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlMessageEventHandler implements MessageEventHandler {
  Pattern twitterPattern = Pattern.compile("https?:\\/\\/(mobile\\.)?twitter\\.com\\/.*?\\/status(es)?\\/(?<statusId>[0-9]+)(\\/photo.*)?");
  private Configuration configuration;

  public UrlMessageEventHandler(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public String getMessageRegex() {
    return null;
  }

  @Override
  public String[] getMessagePrefixes() {
    return new String[] { "http://", "https://", "www." };
  }

  @Override
  public String getDescription() {
    return "send to channel title of any URLs detected";
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
    Matcher matcher = twitterPattern.matcher(url);
    if (matcher.matches()) {
      String statusId = matcher.group("statusId");
      ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
          .setOAuthAccessToken(this.configuration.getTwitterCredentials().getAccessToken())
          .setOAuthAccessTokenSecret(this.configuration.getTwitterCredentials().getAccessTokenSecret())
          .setOAuthConsumerSecret(this.configuration.getTwitterCredentials().getConsumerSecret())
          .setOAuthConsumerKey(this.configuration.getTwitterCredentials().getConsumerKey());
      TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
      Twitter twitter = twitterFactory.getInstance();
      Status status;
      try {
        status = twitter.showStatus(Long.parseLong(statusId));
      } catch (TwitterException e) {
        throw new RuntimeException(e);
      }
      event.getChannel().send().message("@" + status.getUser().getName() + ": " + status.getText());
    } else {
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
}
