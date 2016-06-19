package org.poker.handler

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson.JacksonFactory
import com.google.api.services.youtube.YouTube
import org.jinstagram.Instagram
import org.joda.time.Period
import org.joda.time.format.PeriodFormatterBuilder
import org.jsoup.{Jsoup, UnsupportedMimeTypeException}
import org.poker.ProgramConfiguration
import org.poker.poller.InstagramOembedPatched
import org.poker.util.SimpleRetrier
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import twitter4j.conf.ConfigurationBuilder
import twitter4j.{Twitter, TwitterFactory}

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

class UrlMessageEventHandler(configuration: ProgramConfiguration) extends MessageEventHandler {
  val twitterRegex = "https?:\\/\\/(mobile\\.)?twitter\\.com\\/.*?\\/status(es)?\\/(?<statusId>[0-9]+)(\\/photo.*)?".r
  val youTubeRegex = "(?:http|https|)(?::\\/\\/|)(?:www.|)(?:youtu\\.be\\/|youtube\\.com(?:\\/embed\\/|\\/v\\/|\\/watch\\?v=|\\/ytscreeningroom\\?v=|\\/feeds\\/api\\/videos\\/|\\/user\\S*[^\\w\\-\\s]|\\S*[^\\w\\-\\s]))([\\w\\-]{11})[a-z0-9;:@#?&%=+\\/\\$_.-]*".r
  val instagramRegex = "https?:\\/\\/instagram.com\\/p\\/([^\\/]+)\\/?$".r
  val youTubeClient = this.createYouTube()

  override val helpMessage: Option[String] = None

  override val messageMatchRegex: Regex = "(?i)\\b((?:[a-z][\\w-]+:(?:/{1,3}|[a-z0-9%])|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]))".r

  val twitter = createTwitter()

  override def onMessage(event: MessageReceivedEvent, firstMatch: Match): Unit = {
    val url = getUrl(firstMatch)
    url match {
      case twitterRegex(mobile, skip, statusId, photo) => {
        if (twitter.isDefined) {
          val status = twitter.get.showStatus(statusId.toLong)
          event.getMessage.getChannel.sendMessage(s"@${status.getUser.getName}: ${status.getText}")
        } else {
          event.getMessage.getChannel.sendMessage("twitter disabled: have all the access keys been set?")
        }
      } case youTubeRegex(videoId) => {
        sendYouTube(event, videoId)
      } case instagramRegex(mediaId) => {
        sendInstagram(event, url)
      } case _ => {
        val document = SimpleRetrier.retry(3)(getDocument(url))
        val title = document.title
        event.getMessage.getChannel.sendMessage(s"$title")
      }
    }
  }

  private def getDocument(url: String) = {
    try {
      Jsoup.connect(url).userAgent("Mozilla").get()
    } catch {
      case e: UnsupportedMimeTypeException => throw new ExpectedFailureException()

    }
  }

  private def getUrl(firstMatch: Match) = {
    val url = firstMatch.group(1)
    if (url.contains("://")) {
      url
    } else {
      "http://" + url
    }
  }

  private def sendInstagram(event: MessageReceivedEvent, url: String): Unit = {
    val instagramClient = new Instagram(configuration.instagramClientId.get)
    val instagramOembed = new InstagramOembedPatched()
    val oembedInfo = instagramOembed.getOembedInformation(url)
    val mediaInfo = instagramClient.getMediaInfo(oembedInfo.getMediaId)
    val comments = if (mediaInfo.getData.getComments.getCount == 1) "comment" else "comments"
    event.getMessage.getChannel.sendMessage(s"${mediaInfo.getData.getCaption.getText} | ${mediaInfo.getData.getType} | ${mediaInfo.getData.getComments.getCount} ${comments}")
  }

  private def sendYouTube(event: MessageReceivedEvent, videoId: String): Unit = {
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
      event.getMessage.getChannel.sendMessage(message)
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
