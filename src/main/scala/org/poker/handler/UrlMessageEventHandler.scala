package org.poker.handler

import twitter4j.conf.ConfigurationBuilder
import twitter4j.Twitter
import twitter4j.TwitterFactory
import scala.util.matching.Regex
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.PircBotX
import scala.util.matching.Regex.Match
import org.poker.ProgramConfiguration
import org.jsoup.Jsoup

class UrlMessageEventHandler(configuration: ProgramConfiguration) extends MessageEventHandler {
  val twitterRegex = "https?:\\/\\/(mobile\\.)?twitter\\.com\\/.*?\\/status(es)?\\/(?<statusId>[0-9]+)(\\/photo.*)?".r

  override val helpMessage: Option[String] = None

  override val messageMatchRegex: Regex = "(?i)\\b((?:[a-z][\\w-]+:(?:/{1,3}|[a-z0-9%])|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]))".r

  val twitter = createTwitter()

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: Match): Unit = {
    val url = firstMatch.group(1)
    url match {
      case twitterRegex(mobile, skip, statusId, photo) => {
        if (twitter.isDefined) {
          val status = twitter.get.showStatus(statusId.toLong)
          event.getChannel.send.message(s"@${status.getUser.getName}: ${status.getText}")
        } else {
          event.getChannel.send.message("twitter disabled: have all the access keys been set?")
        }
      } case _ => {
        val document = Jsoup.connect(url).get()
        val title = document.title
        event.getChannel.send.message(s"$title")
      }
    }
  }

  private def createTwitter(): Option[Twitter] = {
    if (configuration.twitterCredentials.isDefined) {
      val twitterCreds = configuration.twitterCredentials.get
      val configurationBuilder = new ConfigurationBuilder()
        .setOAuthAccessToken(twitterCreds.accessToken)
        .setOAuthAccessTokenSecret(twitterCreds.accessTokenSecret)
        .setOAuthConsumerSecret(twitterCreds.consumerSecret)
        .setOAuthConsumerKey(twitterCreds.consumerKey)
      val twitterFactory: TwitterFactory = new TwitterFactory(configurationBuilder.build)
      val twitter = twitterFactory.getInstance
      Option(twitter)
    } else {
      None
    }
  }
}
