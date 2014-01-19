package org.poker.irc.steam;

import com.google.gson.annotations.*;

import java.util.*;

public class MatchDetails {

  @Expose
  private int status;
  @Expose
  private int num_results;
  @Expose
  private int total_results;
  @Expose
  private int results_remaining;
  @Expose
  private List<Match> matches = new ArrayList<Match>();
  @Expose
  private List<Player> players = new ArrayList<Player>();
  @Expose
  private Boolean radiant_win;
  @Expose
  private int duration;
  @Expose
  private int start_time;
  @Expose
  private int match_id;
  @Expose
  private int match_seq_num;
  @Expose
  private int tower_status_radiant;
  @Expose
  private int tower_status_dire;
  @Expose
  private int barracks_status_radiant;
  @Expose
  private int barracks_status_dire;
  @Expose
  private int cluster;
  @Expose
  private int first_blood_time;
  @Expose
  private int lobby_type;
  @Expose
  private int human_players;
  @Expose
  private int leagueid;
  @Expose
  private int positive_votes;
  @Expose
  private int negative_votes;
  @Expose
  private int game_mode;

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getNum_results() {
    return num_results;
  }

  public void setNum_results(int num_results) {
    this.num_results = num_results;
  }

  public int getTotal_results() {
    return total_results;
  }

  public void setTotal_results(int total_results) {
    this.total_results = total_results;
  }

  public int getResults_remaining() {
    return results_remaining;
  }

  public void setResults_remaining(int results_remaining) {
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

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public int getStart_time() {
    return start_time;
  }

  public void setStart_time(int start_time) {
    this.start_time = start_time;
  }

  public int getMatch_id() {
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

  public int getTower_status_radiant() {
    return tower_status_radiant;
  }

  public void setTower_status_radiant(int tower_status_radiant) {
    this.tower_status_radiant = tower_status_radiant;
  }

  public int getTower_status_dire() {
    return tower_status_dire;
  }

  public void setTower_status_dire(int tower_status_dire) {
    this.tower_status_dire = tower_status_dire;
  }

  public int getBarracks_status_radiant() {
    return barracks_status_radiant;
  }

  public void setBarracks_status_radiant(int barracks_status_radiant) {
    this.barracks_status_radiant = barracks_status_radiant;
  }

  public int getBarracks_status_dire() {
    return barracks_status_dire;
  }

  public void setBarracks_status_dire(int barracks_status_dire) {
    this.barracks_status_dire = barracks_status_dire;
  }

  public int getCluster() {
    return cluster;
  }

  public void setCluster(int cluster) {
    this.cluster = cluster;
  }

  public int getFirst_blood_time() {
    return first_blood_time;
  }

  public void setFirst_blood_time(int first_blood_time) {
    this.first_blood_time = first_blood_time;
  }

  public int getLobby_type() {
    return lobby_type;
  }

  public void setLobby_type(int lobby_type) {
    this.lobby_type = lobby_type;
  }

  public int getHuman_players() {
    return human_players;
  }

  public void setHuman_players(int human_players) {
    this.human_players = human_players;
  }

  public int getLeagueid() {
    return leagueid;
  }

  public void setLeagueid(int leagueid) {
    this.leagueid = leagueid;
  }

  public int getPositive_votes() {
    return positive_votes;
  }

  public void setPositive_votes(int positive_votes) {
    this.positive_votes = positive_votes;
  }

  public int getNegative_votes() {
    return negative_votes;
  }

  public void setNegative_votes(int negative_votes) {
    this.negative_votes = negative_votes;
  }

  public int getGame_mode() {
    return game_mode;
  }

  public void setGame_mode(int game_mode) {
    this.game_mode = game_mode;
  }
}
