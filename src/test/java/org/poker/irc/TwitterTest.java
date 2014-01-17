package org.poker.irc;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;

public class TwitterTest {
  private static final Logger logger = LoggerFactory.getLogger(TwitterTest.class);

  @Test
  public void testTwitter() throws TwitterException {
    ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
        .setDebugEnabled(true)
        .setOAuthConsumerKey("")
        .setOAuthConsumerSecret("")
        .setOAuthAccessToken("")
        .setOAuthAccessTokenSecret("");
    TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
    Twitter twitter = twitterFactory.getInstance();
    //Twitter twitter = new Twitter("https://api.twitter.com/1.1/");
    //twitter.setOAuthConsumer("xPSflh20vuMYawYkW35mHw", "Rw1RlJI2xrIyzEHnwa9YQJ2ldUclUdyaeCVAcEks");
    //RequestToken requestToken = twitter.getOAuthRequestToken();
    //AccessToken accessToken = twitter.getOAuthAccessToken(requestToken);
    long statusId = 422882080717291521L;
    twitter.showStatus(statusId);
  }
}
