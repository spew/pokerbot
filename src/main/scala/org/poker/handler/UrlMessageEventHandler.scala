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
import com.google.api.services.youtube.YouTube
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson.JacksonFactory
import org.joda.time.{Period, Duration}
import org.joda.time.format.{PeriodFormatterBuilder, PeriodFormatter}
import org.jinstagram.auth.InstagramAuthService
import org.jinstagram.auth.model.Verifier
import org.jinstagram.{InstagramOembed, Instagram}

class UrlMessageEventHandler(configuration: ProgramConfiguration) extends MessageEventHandler {
  val twitterRegex = "https?:\\/\\/(mobile\\.)?twitter\\.com\\/.*?\\/status(es)?\\/(?<statusId>[0-9]+)(\\/photo.*)?".r
  val youTubeRegex = "(?:http|https|)(?::\\/\\/|)(?:www.|)(?:youtu\\.be\\/|youtube\\.com(?:\\/embed\\/|\\/v\\/|\\/watch\\?v=|\\/ytscreeningroom\\?v=|\\/feeds\\/api\\/videos\\/|\\/user\\S*[^\\w\\-\\s]|\\S*[^\\w\\-\\s]))([\\w\\-]{11})[a-z0-9;:@#?&%=+\\/\\$_.-]*".r
  val instagramRegex = "https?:\\/\\/instagram.com\\/p\\/([^\\/]+)\\/?$".r
  val youTubeClient = this.createYouTube()

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
      } case youTubeRegex(videoId) => {
        sendYouTube(event, videoId)
      } case instagramRegex(mediaId) => {
        sendInstagram(event, url)
      } case _ => {
        val document = Jsoup.connect(url).get()
        val title = document.title
        event.getChannel.send.message(s"$title")
      }
    }
  }

  private def sendInstagram(event: MessageEvent[PircBotX], url: String): Unit = {
    val instagramClient = new Instagram(configuration.instagramClientId.get)
    val instagramOembed = new InstagramOembed()
    val oembedInfo = instagramOembed.getOembedInformation(url)
    val mediaInfo = instagramClient.getMediaInfo(oembedInfo.getMediaId)
    event.getChannel.send.message(s"${mediaInfo.getData.getCaption.getText} - Instagram")
  }

  private def sendYouTube(event: MessageEvent[PircBotX], videoId: String): Unit = {
    val parts = "id,statistics,contentDetails,snippet"
    val request = youTubeClient.videos().list(parts).setId(videoId)
    request.setKey(configuration.googleSearchApiKey.get)
    val videoListResponse = request.execute()
    if (videoListResponse.getItems.size == 1) {
      val video = videoListResponse.getItems.get(0)
      val views = video.getStatistics.getViewCount
      val likeCount = video.getStatistics.getLikeCount
      val dislikeCount = video.getStatistics.getDislikeCount
      val period = new Period(video.getContentDetails.getDuration)
      val title = video.getSnippet.getTitle
      val message = s"${title} | ${formatPeriod(period)} | ${views} views (+${likeCount}, -${dislikeCount})"
      event.getChannel.send.message(message)
    }
  }

  private def formatPeriod(period: Period): String = {
    val formatter = new PeriodFormatterBuilder()
      .printZeroNever()
      .appendDays()
      .appendSuffix("d ")
      .appendHours()
      .minimumPrintedDigits(2)
      .appendSeparator(":")
      .printZeroAlways()
      .appendMinutes()
      .appendSeparator(":")
      .appendSeconds()
      .toFormatter
    formatter.print(period)
  }

  private def createYouTube(): YouTube = {
    val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    val jsonFactory = new JacksonFactory()
    val youTube = new YouTube.Builder(httpTransport, jsonFactory, null)
      .setApplicationName("poker-bot").build()
    youTube
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
