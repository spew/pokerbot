package org.poker.irc.messagehandler;

import org.javatuples.Pair;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.Configuration;
import org.poker.irc.HttpUtils;
import org.poker.irc.MessageEventHandler;
import org.poker.irc.twitch.StreamsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class StreamsMessageEventHandler implements MessageEventHandler {
  private enum Game {
    Dota,
    LeagueOfLegends,
    Quake,
    MagicTheGathering,
    Diablo3,
    Rust,
    Hearthstone,
  }
  private static final Logger LOG = LoggerFactory.getLogger(StreamsMessageEventHandler.class);
  private final Configuration configuration;

  public StreamsMessageEventHandler(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public String getMessageRegex() {
    return null;
  }

  @Override
  public String getDescription() {
    return "!streams <game> or .streams <game>: send to channel top twitch streams for { dota, lol, quake, magic }.";
  }

  @Override
  public String[] getMessagePrefixes() {
    return new String[] { "!twitch", ".twitch", "!streams", ".streams" };
  }

  @Override
  public void onMessage(MessageEvent event) {
    Game game;
    String message = event.getMessage();
    int index = message.indexOf(' ');
    if (index < 0) {
      game = Game.Dota;
    } else {
      // TODO: switch to regex for this stuff
      String gameName = message.substring(index + 1).trim().toLowerCase();
      if (gameName.startsWith("l")) {
        game = Game.LeagueOfLegends;
      } else if (gameName.startsWith("do")) {
        game = Game.Dota;
      } else if (gameName.startsWith("di")) {
        game = Game.Diablo3;
      } else if (gameName.startsWith("q")) {
        game = Game.Quake;
      } else if (gameName.startsWith("mtg") || gameName.startsWith("magic") || gameName.startsWith("m:tg")) {
        game = Game.MagicTheGathering;
      } else if (gameName.startsWith("rust")) {
        game = Game.Rust;
      } else if (gameName.startsWith("hearth")) {
        game = Game.Hearthstone;
      }else if (gameName.equals("poker")) {
        event.getChannel().send().message("Poker? I don't support dead games");
        return;
      } else {
        event.getChannel().send().message("Unknown game: " + gameName);
        return;
      }
    }
    int limit = 3;
    String gameName;
    switch (game) {
      case Dota:
        gameName = "Dota+2";
        break;
      case LeagueOfLegends:
        gameName = "League+of+Legends";
        break;
      case MagicTheGathering:
        gameName = "Magic%3A%20The%20Gathering";
        break;
      case Quake:
        gameName = "Quake%20Live";
        limit = 1;
        break;
      case Rust:
        gameName = "Rust";
        limit = 2;
        break;
      case Hearthstone:
        gameName = "Hearthstone";
        break;
      case Diablo3:
        //StandardCharsets.UTF_8.displayName()
        try {
          gameName = URLEncoder.encode("Diablo III: Reaper of Souls", "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
          throw new RuntimeException(e);
        }
        limit = 3;
        break;
      default:
        throw new NotImplementedException();
    }
    String url = "https://api.twitch.tv/kraken/streams?limit=" + limit + "&game=" + gameName;
    StreamsResponse streamsResponse = HttpUtils.getJson(url, StreamsResponse.class,
        new Pair<String, String>("Client-ID", this.configuration.getTwitchClientId()),
        new Pair<String, String>("Accept", "application/vnd.twitchtv.v2+json"));
    for (StreamsResponse.Stream stream : streamsResponse.getStreams()) {
      StringBuilder sb = new StringBuilder();
      sb.append(stream.getChannel().getDisplay_name());
      sb.append(" | ");
      sb.append(stream.getViewers());
      sb.append(" viewers | ");
      sb.append(stream.getChannel().getUrl());
      sb.append("/popout");
      event.getChannel().send().message(sb.toString());
    }
  }
}
