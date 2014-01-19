package org.poker.irc.steam;

import com.google.gson.annotations.Expose;

import java.util.List;

public class GetMatchHistoryBySequenceNumResponse {
  @Expose
  private GetMatchHistoryBySequenceNumResult result;

  public GetMatchHistoryBySequenceNumResult getResult() {
    return result;
  }

  public static class GetMatchHistoryBySequenceNumResult {
    @Expose
    private int status;
    @Expose
    private List<MatchDetails> matches;

    public List<MatchDetails> getMatches() {
      return matches;
    }

    public int getStatus() {
      return status;
    }
  }
}
