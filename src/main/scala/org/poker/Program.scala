package org.poker

import com.typesafe.scalalogging.slf4j.StrictLogging
import scopt.OptionParser

class TwitterCredentials(val accessToken: String, val accessTokenSecret: String, val consumerKey: String, val consumerSecret: String)

// used for command line parsing
case class ProgramConfiguration(
  nick: String = "testbot",
  finger: String = "stfu pete",
  realName: String = "pete is a donk",
  serverHostname: String = "irc.gamesurge.net",
  channels: Seq[String] = List(),
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
  instagramClientSecret: Option[String] = None
)

object Program extends StrictLogging {
  def main(args: Array[String]) {
    val parser = this.createParser();
    try {
      parser.parse(args, loadDefaultConfiguration()) map { configuration =>
        var finalConfiguration = configuration
        if (configuration.channels.isEmpty) {
          finalConfiguration = configuration.copy(channels = configuration.channels :+ "#pokerbot")
        }
        val botRunner = new BotRunner(finalConfiguration)
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
    c = c.copy(sceneAccessPassword = loadEnvVar("SCC_PASSWORD"))
    c = c.copy(sceneAccessUserName = loadEnvVar("SCC_USERNAME"))
    c = c.copy(steamApiKey = loadEnvVar("STEAM_API_KEY"))
    c = c.copy(googleSearchApiKey = loadEnvVar("SEARCH_API_KEY"))
    c = c.copy(googleSearchCxKey = loadEnvVar("SEARCH_CX_KEY"))
    c = c.copy(twitchClientId = loadEnvVar("TWITCH_CLIENT_ID"))
    c = c.copy(rottenTomatoesApiKey = loadEnvVar("RT_API_KEY"))
    c = c.copy(instagramClientId = loadEnvVar("INSTAGRAM_CLIENT_ID"))
    c = c.copy(instagramClientSecret = loadEnvVar("INSTAGRAM_CLIENT_SECRET"))
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

  def createParser(): OptionParser[ProgramConfiguration] = {
    val parser = new OptionParser[ProgramConfiguration]("pokerbot") {
      head("pokerbot", "1.0")
      opt[String]('n', "nick") optional() action { (n, c) => c.copy(nick = n) } text("nick-name to be used in irc")
      opt[String]('s', "server-hostname") optional() action { (s, c) => c.copy(serverHostname = s) } text("hostname of irc server")
      opt[String]("channels") maxOccurs(10) action {
        (ch, c) => {
          c.copy(channels = c.channels :+ (if (ch.startsWith("#")) ch else "#" + ch))
        }
      } text("channels to join")
      help("help") text("prints usage")
    }
    parser
  }
}