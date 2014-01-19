package org.poker.irc.steam;


import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.poker.irc.Configuration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Dota {
  private Configuration configuration;
  public Dota(Configuration configuration) {
    this.configuration = configuration;
  }

  //API is not giving back the 10 most recent or rather the 10 most recent arent the same as Dotabuff..
  //TODO: Need to weed out the early abandoned matches which dotabuff excludes..
  public List<Match> getMatches(long playerId, int maxResults) {
    List<Match> recentMatches = new ArrayList<Match>();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    MatchHistoryResponse matchHistoryResponse;
    HttpGet httpGet = new HttpGet("https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?" +
        "account_id=" + playerId + "&matches_requested=" + maxResults + "&key=" + configuration.getSteamApiKey() );
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

  public MatchDetails getMatchDetails(long matchId) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    MatchDetailResponse matchDetailResponse;
    HttpGet httpGet = new HttpGet("https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?" +
        "match_id=" + matchId + "&key=" + this.configuration.getSteamApiKey());
    httpGet.addHeader("Accept", "application/json");
    try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(httpGet)) {
      HttpEntity httpEntity = response.getEntity();
      try (Reader reader = new InputStreamReader(httpEntity.getContent())) {
        matchDetailResponse = gson.fromJson(reader, MatchDetailResponse.class);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return matchDetailResponse.getResult();
  }
}
