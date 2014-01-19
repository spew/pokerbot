package org.poker.irc.messagehandler;

import com.google.api.client.util.*;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.gson.*;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.Configuration;
import org.poker.irc.MessageEventHandler;
import org.poker.irc.steam.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DotabuffMessageEventHandler implements MessageEventHandler {
  private static final Logger LOG = LoggerFactory.getLogger(DotabuffMessageEventHandler.class);
  private Map<String, Integer> nameToId = Maps.newHashMap();
  private Dota dota;
  private enum MatchResult {
    WIN,
    LOSS
  }
  private MatchResult streakType;

  public DotabuffMessageEventHandler(Configuration configuration) {
    this.populateDefaultPlayers();
    this.dota = new Dota(configuration);
  }

  @Override
  public String[] getMessagePrefixes() {
    return new String[] { "!dota ", "!dotabuff ", ".dota ", ".dotabuff "};
  }

  @Override
  public String getDescription() {
    return "!dotabuff <name> or .dotabuff <name> : gives you the wins, losses and dotabuff url for that player.";
  }

  @Override
  public void onMessage(MessageEvent event) {
    String message = event.getMessage();
    if (message.startsWith("!dotabuff")) {
      message = message.substring("!dotabuff".length()).trim();
    } else if (message.startsWith(".dotabuff")) {
      message = message.substring(".dotabuff".length()).trim();
    } else if (message.startsWith("!dota")) {
      message = message.substring("!dota".length()).trim();
    } else {
      message = message.substring(".dota".length()).trim();
    }
    message = message.toLowerCase();
    if (this.nameToId.containsKey(message)) {
      Integer playerId = this.nameToId.get(message);
      Integer currentStreak = getCurrentStreak(playerId);
      String url = "http://dotabuff.com/players/" + playerId;
      Document document;
      try {
        document = Jsoup.connect(url).get();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      Element wonSpan = document.select("span.won").first();
      String gamesWon = wonSpan.text();
      Element lostSpan = document.select("span.lost").first();
      String gamesLost = lostSpan.text();
      event.getChannel().send().message(url);
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("wins: ");
      stringBuilder.append(gamesWon);
      stringBuilder.append(" | ");
      stringBuilder.append("losses: ");
      stringBuilder.append(gamesLost);
      stringBuilder.append(" |  Streak: ");
      stringBuilder.append(currentStreak.toString());
      stringBuilder.append(" Game ");
      stringBuilder.append(streakType.toString());
      stringBuilder.append(" streak.");
      event.getChannel().send().message(stringBuilder.toString());
      LOG.warn(currentStreak.toString() + streakType.toString());
    } else {
      event.getChannel().send().message("Unknown player name: " + message);
    }
  }

  private void populateDefaultPlayers() {
    this.nameToId.put("whitey", 38926297);
    this.nameToId.put("pete", 38926297);
    this.nameToId.put("bertkc", 80342375);
    this.nameToId.put("brett", 80342375);
    this.nameToId.put("bank", 80342375);
    this.nameToId.put("mike", 28308237);
    this.nameToId.put("fud", 10648475);
    this.nameToId.put("spew", 10648475);
    this.nameToId.put("deathdealer69", 10648475);
    this.nameToId.put("steven", 28326143);
    this.nameToId.put("bunk", 28326143);
    this.nameToId.put("clock", 125412282);
    this.nameToId.put("cl0ck", 125412282);
    this.nameToId.put("muiy", 78932949);
    this.nameToId.put("dank", 78932949);
    this.nameToId.put("viju", 34117856);
    this.nameToId.put("vijal", 34117856);
    this.nameToId.put("sysm", 29508928);
    this.nameToId.put("rtz", 86745912);
    this.nameToId.put("arteezy", 86745912);
  }

  private List<MatchResult> getRecentResults(long playerId, List<Match> recentMatches) {
    List<MatchResult> results = Lists.newArrayList();
    for (Match match: recentMatches) {
      int matchId = match.getMatch_id();
      MatchResult currentResult = checkMatchResult(matchId, playerId);
      results.add(currentResult);
    }
    return results;
  }

  private MatchResult checkMatchResult(long matchId, long playerId) {
    List<Player> players = new ArrayList<>();
    Stopwatch stopwatch = Stopwatch.createStarted();
    MatchDetails result = this.dota.getMatchDetails(matchId);
    stopwatch.stop();
    LOG.info("getMatchDetails length: {}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    boolean radiantWin = result.getRadiant_win();
    players =  result.getPlayers();
    for (Player player : players) {
      if (player.getAccount_id() == playerId) {
        if (player.getPlayer_slot() < 128) {
          return radiantWin ? MatchResult.WIN : MatchResult.LOSS;
        } else {
          return radiantWin ? MatchResult.LOSS : MatchResult.WIN;
        }
      }
    }
    throw new RuntimeException("Player id not found: " + playerId);
  }

  private int getCurrentStreak(long playerId) {
    int streak = 0;
    List<Match> recentMatches = this.dota.getMatches(playerId, 10);
    //Map<String, String> matchesWithPlayerSlot = this.getMatchesWithPlayerSlot(recentMatches, playerId);
    List<MatchResult> recentResults = getRecentResults(playerId,recentMatches);
    MatchResult prev = recentResults.get(0);
    for (MatchResult result : recentResults) {
      if (result.equals(prev)) {
        streak++;
      } else{
        break;
      }
      prev = result;
    }
    this.streakType = recentResults.get(0);
    return streak ;
  }
}
