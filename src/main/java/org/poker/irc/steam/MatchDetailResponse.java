package org.poker.irc.steam;

import com.google.gson.annotations.*;

public class MatchDetailResponse {
  @Expose
  private MatchDetails result;

  public MatchDetails getResult() {
    return result;
  }

  public void setResult(MatchDetails result) {
    this.result = result;
  }
}