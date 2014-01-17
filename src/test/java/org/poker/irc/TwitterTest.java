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
        .setOAuthConsumerKey("xPSflh20vuMYawYkW35mHw")
        .setOAuthConsumerSecret("Rw1RlJI2xrIyzEHnwa9YQJ2ldUclUdyaeCVAcEks")
        .setOAuthAccessToken("2294038668-k5anOgUPl1eVyZyYpvX2kDs12WKmajKalusUicN")
        .setOAuthAccessTokenSecret("lZu3JDDEnyL0YeFneDl6TDHUsKiCkUx1BUqwzArjA8wMa");
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
