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
  private final String STEAM_API_KEY = System.getenv("STEAM_API_KEY");
  private enum Result {
    WIN,
    LOSS
  }
  private Result streakType;

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
  //API is not giving back the 10 most recent or rather the 10 most recent arent the same as Dotabuff..
  //TODO: Need to weed out the early abandoned matches which dotabuff excludes..
  private List<Match> getRecentMatches(String playerId){
    List<Match> recentMatches = new ArrayList<Match>();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    MatchHistoryResponse matchHistoryResponse;
    //10 results for now, defaults to 100
    HttpGet httpGet = new HttpGet("https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?" +
                                    "account_id=" + playerId + "&matches_requested=10&key=" + STEAM_API_KEY );
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

  private List<Result> getRecentResults(Map<String,String> matchesWithPlayerSlot){
    List<Result> results = new ArrayList<>();
    Result currentResult;
    for(Map.Entry<String,String> entry : matchesWithPlayerSlot.entrySet()){
      currentResult = checkMatchResult(entry.getKey(),entry.getValue());
      results.add(currentResult);
    }
    return results;
  }

  private Map<String,String> getMatchesWithPlayerSlot(List<Match> matches, String playerId){
    Map<String,String> matchesWithPlayerSlot = Maps.newHashMap();
    List<Player> players = new ArrayList<>();
    String playerSlot = null;
    for(Match match : matches){
      players = match.getPlayers();
      playerSlot = this.getPlayerSlot(players,playerId);
      matchesWithPlayerSlot.put(match.getMatch_id().toString(), playerSlot);
    }
    return matchesWithPlayerSlot;
  }

  private Result checkMatchResult(String matchId, String playerSlot){
    Result result = null;
    boolean radiantWin;
    List<Player> players = new ArrayList<>();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    MatchDetailResponse matchDetailResponse ;

    HttpGet httpGet = new HttpGet("https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?" +
                                  "match_id=" + matchId + "&key=" + STEAM_API_KEY );
    httpGet.addHeader("Accept", "application/json");
    try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(httpGet)) {
      HttpEntity httpEntity = response.getEntity();
      try (Reader reader = new InputStreamReader(httpEntity.getContent())) {
        matchDetailResponse = gson.fromJson(reader, MatchDetailResponse.class);
        radiantWin = matchDetailResponse.getResult().getRadiant_win();
        players =  matchDetailResponse.getResult().getPlayers();
        for(Player player : players){
          Integer currentPlayerSlot = player.getPlayer_slot();
          if(currentPlayerSlot.toString().equals(playerSlot)){
            if(currentPlayerSlot < 128 && radiantWin || currentPlayerSlot > 4 && !(radiantWin)){
              result = Result.WIN;
            }else{
              result = Result.LOSS;
            }
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return result;
  }
//0-4 radiant 128-143 dire
  private String getPlayerSlot(List<Player> players, String playerId){
    String playerSlot = null;
    long playerAccount;
    for(Player player : players){
      //?? fml
      //THIS BUG:  http://stackoverflow.com/questions/20181259/java-gson-parse-not-deserializing-correctly
      playerAccount = player.getAccount_id().longValue();
      if (String.valueOf(playerAccount).equals(playerId)){
        playerSlot = player.getPlayer_slot().toString();
        break;
      }
    }
    return playerSlot;
  }

  private int getCurrentStreak(String playerId){
    int streak = 0;
    List<Match> recentMatches = this.getRecentMatches(playerId);
    Map<String,String> matchesWithPlayerSlot = this.getMatchesWithPlayerSlot(recentMatches,playerId);
    List<Result> recentResults = getRecentResults(matchesWithPlayerSlot);
    Result prev = recentResults.get(0);
    for(Result result : recentResults){
      if(result.equals(prev)){
        streak++;
      }else{
        break;
      }
      prev = result;
    }
    this.streakType = recentResults.get(0);
    return streak ;
  }
}
