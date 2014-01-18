package org.poker.irc.steam;

import com.google.gson.annotations.*;

import java.util.*;

public class Match {
  @Expose
  private Integer match_id;
  @Expose
  private Integer match_seq_num;
  @Expose
  private Integer start_time;
  @Expose
  private Integer lobby_type;
  @Expose
  private List<Player> players = new ArrayList<Player>();

  public Integer getMatch_id() {
    return match_id;
  }

  public void setMatch_id(Integer match_id) {
    this.match_id = match_id;
  }

  public Integer getMatch_seq_num() {
    return match_seq_num;
  }

  public void setMatch_seq_num(Integer match_seq_num) {
    this.match_seq_num = match_seq_num;
  }

  public Integer getStart_time() {
    return start_time;
  }

  public void setStart_time(Integer start_time) {
    this.start_time = start_time;
  }

  public Integer getLobby_type() {
    return lobby_type;
  }

  public void setLobby_type(Integer lobby_type) {
    this.lobby_type = lobby_type;
  }

  public List<Player> getPlayers() {
    return players;
  }

  public void setPlayers(List<Player> players) {
    this.players = players;
  }
}
