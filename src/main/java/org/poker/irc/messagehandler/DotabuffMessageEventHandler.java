package org.poker.irc.messagehandler;

import com.google.common.collect.Maps;
import com.google.gson.*;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pircbotx.*;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.MessageEventHandler;
import org.poker.irc.espn.*;
import org.poker.irc.steam.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;

public class DotabuffMessageEventHandler implements MessageEventHandler {
  private static final Logger LOG = LoggerFactory.getLogger(DotabuffMessageEventHandler.class);
  private Map<String, String> nameToId = Maps.newHashMap();

  public DotabuffMessageEventHandler() {
    this.populateDefaultPlayers();
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
      String playerId = this.nameToId.get(message);
      String url = "http://dotabuff.com/players/" + playerId;
      List<Match> recentMatches = new ArrayList<>();
      recentMatches = getRecentMatches(playerId);
      for(Match match : recentMatches){
        LOG.warn(match.getMatch_id().toString());
      }
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
      stringBuilder.append((" | "));
      stringBuilder.append("losses: ");
      stringBuilder.append(gamesLost);
      event.getChannel().send().message(stringBuilder.toString());
    } else {
      event.getChannel().send().message("Unknown player name: " + message);
    }
  }

  private void populateDefaultPlayers() {
    this.nameToId.put("whitey", "38926297");
    this.nameToId.put("pete", "38926297");
    this.nameToId.put("bertkc", "80342375");
    this.nameToId.put("brett", "80342375");
    this.nameToId.put("bank", "80342375");
    this.nameToId.put("mike", "28308237");
    this.nameToId.put("fud", "10648475");
    this.nameToId.put("spew", "10648475");
    this.nameToId.put("deathdealer69", "10648475");
    this.nameToId.put("steven", "28326143");
    this.nameToId.put("clock", "125412282");
    this.nameToId.put("cl0ck", "125412282");
    this.nameToId.put("muiy", "78932949");
    this.nameToId.put("dank", "78932949");
  }
  private List<Match> getRecentMatches(String playerId){
    String STEAM_API_KEY = System.getenv("STEAM_API_KEY");
    List<Match> recentMatches = new ArrayList<Match>();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    MatchHistoryResponse matchHistoryResponse;
    HttpGet httpGet = new HttpGet("https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?" +
                                    "account_id=" + playerId + "&key=" + STEAM_API_KEY );
    httpGet.addHeader("Accept", "application/json");
    try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(httpGet)) {
      HttpEntity httpEntity = response.getEntity();
      try (Reader reader = new InputStreamReader(httpEntity.getContent())) {
        matchHistoryResponse = gson.fromJson(reader, MatchHistoryResponse.class);
        recentMatches = matchHistoryResponse.getResult().getMatches();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return recentMatches;
  }

  private String getCurrentStreak(String playerId){
    return null;
  }
}
