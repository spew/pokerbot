package org.poker

import com.typesafe.scalalogging.slf4j.StrictLogging
import scopt.OptionParser

class TwitterCredentials(val accessToken: String, val accessTokenSecret: String, val consumerKey: String, val consumerSecret: String)

// used for command line parsing
case class ProgramConfiguration(
  discordToken: Option[String] = None,
  steamApiKey: Option[String] = None,
  sceneAccessUserName: Option[String] = None,
  sceneAccessPassword: Option[String] = None,
  googleSearchApiKey: Option[String] = None,
  googleSearchCxKey: Option[String] = None,
  twitterCredentials: Option[TwitterCredentials] = None,
  cryptoMarketCapRefreshIntervalMinutes: Int = 5,
  twitchClientId: Option[String] = None,
  rottenTomatoesApiKey: Option[String] = None,
  instagramClientId: Option[String] = None,
  instagramClientSecret: Option[String] = None,
  untappdClientId: Option[String] = None,
  untappdClientSecret: Option[String] = None,
  untappdAccessToken: Option[String] = None,
  yahooConsumerKey: Option[String] = None,
  yahooConsumerSecret: Option[String] = None,
  testMode: Boolean = false
)

object Program extends StrictLogging {
  def main(args: Array[String]) {
    try {
      val parser = this.createParser();
      parser.parse(args, loadDefaultConfiguration()) map { configuration =>
        val botRunner = new BotRunner(configuration)
        botRunner.run()
      } getOrElse {
        logger.warn("Unable to properly parse arguments, exiting...")
      }
    } catch {
      case t: Throwable =>
        logger.error("Uncaught exception in main thread", t)
        throw t
    }
  }

  private def loadDefaultConfiguration(): ProgramConfiguration = {
    var c = ProgramConfiguration()
    c = c.copy(discordToken = loadEnvVar("DISCORD_TOKEN"))
    c = c.copy(sceneAccessPassword = loadEnvVar("SCC_PASSWORD"))
    c = c.copy(sceneAccessUserName = loadEnvVar("SCC_USERNAME"))
    c = c.copy(steamApiKey = loadEnvVar("STEAM_API_KEY"))
    c = c.copy(googleSearchApiKey = loadEnvVar("SEARCH_API_KEY"))
    c = c.copy(googleSearchCxKey = loadEnvVar("SEARCH_CX_KEY"))
    c = c.copy(twitchClientId = loadEnvVar("TWITCH_CLIENT_ID"))
    c = c.copy(rottenTomatoesApiKey = loadEnvVar("RT_API_KEY"))
    c = c.copy(instagramClientId = loadEnvVar("INSTAGRAM_CLIENT_ID"))
    c = c.copy(instagramClientSecret = loadEnvVar("INSTAGRAM_CLIENT_SECRET"))
    c = c.copy(untappdClientId = loadEnvVar("UNTAPPED_CLIENT_ID"))
    c = c.copy(untappdClientSecret = loadEnvVar("UNTAPPED_CLIENT_SECRET"))
    c = c.copy(untappdAccessToken = loadEnvVar("UNTAPPED_ACCESS_TOKEN"))
    c = c.copy(yahooConsumerKey = loadEnvVar("YAHOO_CONSUMER_KEY"))
    c = c.copy(yahooConsumerSecret = loadEnvVar("YAHOO_CONSUMER_SECRET"))
    val twitterAccessToken = loadEnvVar("TWITTER_OAUTH_ACCESS_TOKEN")
    val twitterAccessTokenSecret = loadEnvVar("TWITTER_OAUTH_ACCESS_TOKEN_SECRET")
    val twitterConsumerKey = loadEnvVar("TWITTER_OAUTH_CONSUMER_KEY")
    val twitterConsumerSecret = loadEnvVar("TWITTER_OAUTH_CONSUMER_SECRET")
    if (twitterAccessToken.isDefined && twitterAccessTokenSecret.isDefined && twitterConsumerKey.isDefined && twitterConsumerSecret.isDefined) {
      c = c.copy(twitterCredentials = Option(new TwitterCredentials(twitterAccessToken.get, twitterAccessTokenSecret.get, twitterConsumerKey.get, twitterConsumerSecret.get)))
    }
    c
  }

  private def loadEnvVar(varName: String): Option[String] = {
    Option(if (sys.env.contains(varName)) sys.env(varName) else null)
  }

  private def createParser(): OptionParser[ProgramConfiguration] = {
    val parser = new OptionParser[ProgramConfiguration]("pokerbot") {
      head("pokerbot", "1.0")
      opt[Boolean] ("test-mode") optional() maxOccurs(1) action { (t, c) => c.copy(testMode = t) } text("run in test mode")
      help("help") text("print usage")
    }
    parser
  }
}