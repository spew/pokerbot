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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;


public class Configuration {
  private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);
  private String serverHostname = "irc.enterthegame.com";
  private List<String> channels = Arrays.asList(new String[] { "#test" });
  private String nick = "testbot";
  private String googleSearchApiKey;
  private String googleSearchCxKey;

  public Configuration() {

  }

  public void initialize(String[] args) {
    String arguments = Joiner.on(' ').join(args);
    LOG.info("Arguments: " + arguments);
    this.googleSearchApiKey = System.getenv().get("SEARCH_API_KEY");
    this.googleSearchCxKey = System.getenv().get("SEARCH_CX_KEY");
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
    if (commandLine.hasOption(channelsOption.getOpt())) {
      List<String> channels = Arrays.asList(commandLine.getOptionValues(channelsOption.getOpt()));
      if (channels.size() > 0) {
        this.channels = channels;
      }
    }
    if (commandLine.hasOption(serverHostNameOption.getOpt())) {
      this.serverHostname = commandLine.getOptionValue(serverHostNameOption.getValue());
    }
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

}
