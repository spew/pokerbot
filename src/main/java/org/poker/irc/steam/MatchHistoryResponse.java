package org.poker.irc.steam;

import com.google.gson.annotations.*;

public class MatchHistoryResponse {
  @Expose
  private Result result;

  public Result getResult() {
    return result;
  }

  public void setResult(Result result) {
    this.result = result;
  }
}
