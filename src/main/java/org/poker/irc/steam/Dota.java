package org.poker.irc.steam;


import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.javatuples.Pair;
import org.poker.irc.Configuration;
import org.poker.irc.HttpUtils;

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
    String url = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?" +
        "account_id=" + playerId + "&matches_requested=" + maxResults + "&key=" + configuration.getSteamApiKey();
    MatchHistoryResponse matchHistoryResponse = HttpUtils.getJson(url, MatchHistoryResponse.class);
    return matchHistoryResponse.getResult().getMatches();
  }

  public MatchDetails getMatchDetails(long matchId) {
    String url = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?" +
        "match_id=" + matchId + "&key=" + this.configuration.getSteamApiKey();
    MatchDetailResponse matchDetailResponse = HttpUtils.getJson(url, MatchDetailResponse.class);
    return matchDetailResponse.getResult();
  }
}
