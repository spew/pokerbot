package org.poker.irc.steam;

import com.google.gson.annotations.*;

import java.util.*;

public class Match {
  @Expose
  private int match_id;
  @Expose
  private int match_seq_num;
  @Expose
  private int start_time;
  @Expose
  private int lobby_type;
  @Expose
  private List<Player> players;

  public Integer getMatch_id() {
    return match_id;
  }

  public void setMatch_id(int match_id) {
    this.match_id = match_id;
  }

  public int getMatch_seq_num() {
    return match_seq_num;
  }

  public void setMatch_seq_num(int match_seq_num) {
    this.match_seq_num = match_seq_num;
  }

  public int getStart_time() {
    return start_time;
  }

  public void setStart_time(int start_time) {
    this.start_time = start_time;
  }

  public int getLobby_type() {
    return lobby_type;
  }

  public void setLobby_type(int lobby_type) {
    this.lobby_type = lobby_type;
  }

  public List<Player> getPlayers() {
    return players;
  }

  public void setPlayers(List<Player> players) {
    this.players = players;
  }
}
