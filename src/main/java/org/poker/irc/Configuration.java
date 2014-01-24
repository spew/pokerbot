package org.poker.irc;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.Lists;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.validator.routines.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;


public class Configuration {
  private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);
  private String serverHostname = "irc.gamesurge.net";
  private List<String> channels = Arrays.asList(new String[] { "#pokerbot" });
  private String nick = "testbot";
  private String googleSearchApiKey;
  private String googleSearchCxKey;
  private String ident = nick;
  private String espnApiKey;
  private String twitchClientId;
  private String steamApiKey;
  private int espnPollIntervalMinutes = 10;
  private List<String> performActions;
  private TwitterCredentials twitterCredentials = new TwitterCredentials();

  public static class TwitterCredentials {
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;

    public String getAccessTokenSecret() {
      return accessTokenSecret;
    }

    public String getConsumerKey() {
      return consumerKey;
    }

    public String getConsumerSecret() {
      return consumerSecret;
    }

    public String getAccessToken() {
      return accessToken;
    }
  }

  public Configuration() {

  }

  public TwitterCredentials getTwitterCredentials() {
    return this.twitterCredentials;
  }

  public String getEspnApiKey() {
    return espnApiKey;
  }

  public String getTwitchClientId() {
    return twitchClientId;
  }

  public List<String> getPerformActions() { return this.performActions; }

  private List<String> parsePerformActions(CommandLine commandLine, Option performOption) {
    if (commandLine.hasOption(performOption.getOpt())) {
      List<String> actions = Arrays.asList(commandLine.getOptionValues(performOption.getOpt()));
      return actions;
    } else {
      String raw = System.getenv("PERFORM");
      if (Strings.isNullOrEmpty(raw)) {
        return Lists.newArrayList();
      }
      return Arrays.asList(raw.split(","));
    }
  }

  public void initialize(String[] args) {
    String arguments = Joiner.on(' ').join(args);
    LOG.info("Arguments: " + arguments);
    this.googleSearchApiKey = System.getenv().get("SEARCH_API_KEY");
    this.googleSearchCxKey = System.getenv().get("SEARCH_CX_KEY");
    this.twitchClientId = System.getenv().get("TWITCH_CLIENT_ID");
    this.espnApiKey = System.getenv("ESPN_API_KEY");
    this.twitterCredentials.accessToken = System.getenv("TWITTER_OAUTH_ACCESS_TOKEN");
    this.twitterCredentials.accessTokenSecret = System.getenv("TWITTER_OAUTH_ACCESS_TOKEN_SECRET");
    this.twitterCredentials.consumerKey = System.getenv("TWITTER_OAUTH_CONSUMER_KEY");
    this.twitterCredentials.consumerSecret = System.getenv("TWITTER_OAUTH_CONSUMER_SECRET");
    this.steamApiKey = System.getenv("STEAM_API_KEY");
    Options options = new Options();
    Option googleSearchApiKeyOption = OptionBuilder
        .withLongOpt("google-search-api-key")
        .hasArg()
        .withArgName("google search API key")
        .withDescription("Google custom search API key")
        .create("gapi");
    options.addOption(googleSearchApiKeyOption);
    Option googleSearchCxKeyOption = OptionBuilder
        .withLongOpt("google-search-cx-key")
        .hasArg()
        .withArgName("google search CX key")
        .withDescription("Google custom search CX key")
        .create("gcx");
    options.addOption(googleSearchCxKeyOption);
    Option helpOption = OptionBuilder
        .withLongOpt("help")
        .withDescription("print this message")
        .create("h");
    options.addOption(helpOption);
    Option nickOption = OptionBuilder
        .withLongOpt("nick")
        .hasArg()
        .withArgName("nick")
        .withDescription("The /nick value to use on IRC")
        .create("n");
    options.addOption(nickOption);
    Option identOption = OptionBuilder
        .withLongOpt("ident")
        .hasArg()
        .withArgName("ident")
        .withDescription("The <ident>@hostmask value to use on IRC")
        .create("i");
    options.addOption(identOption);
    Option performOption = OptionBuilder
        .withLongOpt("perform-commands")
        .hasArgs()
        .withValueSeparator(',')
        .withDescription("A command that will be performed upon successful connection")
        .create("p");
    options.addOption(performOption);
    Option channelsOption = OptionBuilder
        .withLongOpt("channels")
        .hasArgs()
        .withValueSeparator()
        .withDescription("The list of channels to join")
        .create("c");
    options.addOption(channelsOption);
    Option serverHostNameOption = OptionBuilder
        .withLongOpt("server-hostname")
        .hasArg()
        .withArgName("hostname")
        .withDescription("The hostname of the IRC server to connect to")
        .create("s");
    options.addOption(serverHostNameOption);
    Option espnPollIntervalOption = OptionBuilder
        .withLongOpt("espn-poll-interval")
        .hasArg()
        .withArgName("interval")
        .withDescription("The interval by which to poll to the ESPN API")
        .create("ei");
    options.addOption(espnPollIntervalOption);
    CommandLineParser parser = new BasicParser();
    CommandLine commandLine;
    try {
     commandLine = parser.parse(options, args);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    if (commandLine.hasOption("help")) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp( "java org.poker.irc.Program", options );
      System.exit(0);
    }
    if (commandLine.hasOption(googleSearchApiKeyOption.getOpt())) {
      this.googleSearchApiKey = commandLine.getOptionValue(googleSearchApiKeyOption.getValue());
    }
    if (commandLine.hasOption(googleSearchCxKeyOption.getOpt())) {
      this.googleSearchCxKey = commandLine.getOptionValue(googleSearchCxKeyOption.getValue());
    }
    if (commandLine.hasOption(nickOption.getOpt())) {
      this.nick = commandLine.getOptionValue(nickOption.getOpt());
    }
    if (commandLine.hasOption(identOption.getOpt())) {
      this.ident = commandLine.getOptionValue(identOption.getOpt());
    } else {
      this.ident = this.nick;
    }
    if (commandLine.hasOption(channelsOption.getOpt())) {
      List<String> channels = Arrays.asList(commandLine.getOptionValues(channelsOption.getOpt()));
      if (channels.size() > 0) {
        this.channels = Lists.newArrayList();
        for (String channel : channels) {
          if (!channel.startsWith("#")) {
            channel = "#" + channel;
          }
          this.channels.add(channel);
        }
      }
    }
    if (commandLine.hasOption(serverHostNameOption.getOpt())) {
      this.serverHostname = commandLine.getOptionValue(serverHostNameOption.getOpt());
    }
    if (commandLine.hasOption(espnPollIntervalOption.getOpt())) {
      this.espnPollIntervalMinutes =
          IntegerValidator.getInstance().validate(commandLine.getOptionValue(espnPollIntervalOption.getOpt()));
    }
    this.performActions = this.parsePerformActions(commandLine, performOption);
  }

  public int getEspnPollIntervalMinutes() {
    return this.espnPollIntervalMinutes;
  }

  public String getGoogleSearchCxKey() {
    return googleSearchCxKey;
  }

  public String getServerHostname() {
    return serverHostname;
  }

  public List<String> getChannels() {
    return ImmutableList.copyOf(channels);
  }

  public String getNick() {
    return nick;
  }

  public String getGoogleSearchApiKey() {
    return googleSearchApiKey;
  }

  public String getIdent() {
    return ident;
  }

  public String getSteamApiKey() { return this.steamApiKey; }
}
