package org.poker.irc.steam;

import com.google.gson.annotations.*;

import java.util.*;

public class Result {

  @Expose
  private Integer status;
  @Expose
  private Integer num_results;
  @Expose
  private Integer total_results;
  @Expose
  private Integer results_remaining;
  @Expose
  private List<Match> matches = new ArrayList<Match>();
  @Expose
  private List<Player> players = new ArrayList<Player>();
  @Expose
  private Boolean radiant_win;
  @Expose
  private Integer duration;
  @Expose
  private Integer start_time;
  @Expose
  private Integer match_id;
  @Expose
  private Integer match_seq_num;
  @Expose
  private Integer tower_status_radiant;
  @Expose
  private Integer tower_status_dire;
  @Expose
  private Integer barracks_status_radiant;
  @Expose
  private Integer barracks_status_dire;
  @Expose
  private Integer cluster;
  @Expose
  private Integer first_blood_time;
  @Expose
  private Integer lobby_type;
  @Expose
  private Integer human_players;
  @Expose
  private Integer leagueid;
  @Expose
  private Integer positive_votes;
  @Expose
  private Integer negative_votes;
  @Expose
  private Integer game_mode;

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Integer getNum_results() {
    return num_results;
  }

  public void setNum_results(Integer num_results) {
    this.num_results = num_results;
  }

  public Integer getTotal_results() {
    return total_results;
  }

  public void setTotal_results(Integer total_results) {
    this.total_results = total_results;
  }

  public Integer getResults_remaining() {
    return results_remaining;
  }

  public void setResults_remaining(Integer results_remaining) {
    this.results_remaining = results_remaining;
  }

  public List<Match> getMatches() {
    return matches;
  }

  public void setMatches(List<Match> matches) {
    this.matches = matches;
  }
  public List<Player> getPlayers() {
    return players;
  }

  public void setPlayers(List<Player> players) {
    this.players = players;
  }

  public Boolean getRadiant_win() {
    return radiant_win;
  }

  public void setRadiant_win(Boolean radiant_win) {
    this.radiant_win = radiant_win;
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public Integer getStart_time() {
    return start_time;
  }

  public void setStart_time(Integer start_time) {
    this.start_time = start_time;
  }

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

  public Integer getTower_status_radiant() {
    return tower_status_radiant;
  }

  public void setTower_status_radiant(Integer tower_status_radiant) {
    this.tower_status_radiant = tower_status_radiant;
  }

  public Integer getTower_status_dire() {
    return tower_status_dire;
  }

  public void setTower_status_dire(Integer tower_status_dire) {
    this.tower_status_dire = tower_status_dire;
  }

  public Integer getBarracks_status_radiant() {
    return barracks_status_radiant;
  }

  public void setBarracks_status_radiant(Integer barracks_status_radiant) {
    this.barracks_status_radiant = barracks_status_radiant;
  }

  public Integer getBarracks_status_dire() {
    return barracks_status_dire;
  }

  public void setBarracks_status_dire(Integer barracks_status_dire) {
    this.barracks_status_dire = barracks_status_dire;
  }

  public Integer getCluster() {
    return cluster;
  }

  public void setCluster(Integer cluster) {
    this.cluster = cluster;
  }

  public Integer getFirst_blood_time() {
    return first_blood_time;
  }

  public void setFirst_blood_time(Integer first_blood_time) {
    this.first_blood_time = first_blood_time;
  }

  public Integer getLobby_type() {
    return lobby_type;
  }

  public void setLobby_type(Integer lobby_type) {
    this.lobby_type = lobby_type;
  }

  public Integer getHuman_players() {
    return human_players;
  }

  public void setHuman_players(Integer human_players) {
    this.human_players = human_players;
  }

  public Integer getLeagueid() {
    return leagueid;
  }

  public void setLeagueid(Integer leagueid) {
    this.leagueid = leagueid;
  }

  public Integer getPositive_votes() {
    return positive_votes;
  }

  public void setPositive_votes(Integer positive_votes) {
    this.positive_votes = positive_votes;
  }

  public Integer getNegative_votes() {
    return negative_votes;
  }

  public void setNegative_votes(Integer negative_votes) {
    this.negative_votes = negative_votes;
  }

  public Integer getGame_mode() {
    return game_mode;
  }

  public void setGame_mode(Integer game_mode) {
    this.game_mode = game_mode;
  }
}
