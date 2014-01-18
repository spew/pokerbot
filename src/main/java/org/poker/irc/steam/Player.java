package org.poker.irc.steam;

import com.google.gson.annotations.*;

import java.util.*;

public class Player {
  @Expose
  private Double account_id;
  @Expose
  private Integer player_slot;
  @Expose
  private Integer hero_id;
  @Expose
  private Integer item_0;
  @Expose
  private Integer item_1;
  @Expose
  private Integer item_2;
  @Expose
  private Integer item_3;
  @Expose
  private Integer item_4;
  @Expose
  private Integer item_5;
  @Expose
  private Integer kills;
  @Expose
  private Integer deaths;
  @Expose
  private Integer assists;
  @Expose
  private Integer leaver_status;
  @Expose
  private Integer gold;
  @Expose
  private Integer last_hits;
  @Expose
  private Integer denies;
  @Expose
  private Integer gold_per_min;
  @Expose
  private Integer xp_per_min;
  @Expose
  private Integer gold_spent;
  @Expose
  private Integer hero_damage;
  @Expose
  private Integer tower_damage;
  @Expose
  private Integer hero_healing;
  @Expose
  private Integer level;
  @Expose
  private List<Ability_upgrade> ability_upgrades = new ArrayList<Ability_upgrade>();

  public Double getAccount_id() {
    return account_id;
  }

  public void setAccount_id(Double account_id) {
    this.account_id = account_id;
  }

  public Integer getPlayer_slot() {
    return player_slot;
  }

  public void setPlayer_slot(Integer player_slot) {
    this.player_slot = player_slot;
  }

  public Integer getHero_id() {
    return hero_id;
  }

  public void setHero_id(Integer hero_id) {
    this.hero_id = hero_id;
  }

  public Integer getItem_0() {
    return item_0;
  }

  public void setItem_0(Integer item_0) {
    this.item_0 = item_0;
  }

  public Integer getItem_1() {
    return item_1;
  }

  public void setItem_1(Integer item_1) {
    this.item_1 = item_1;
  }

  public Integer getItem_2() {
    return item_2;
  }

  public void setItem_2(Integer item_2) {
    this.item_2 = item_2;
  }

  public Integer getItem_3() {
    return item_3;
  }

  public void setItem_3(Integer item_3) {
    this.item_3 = item_3;
  }

  public Integer getItem_4() {
    return item_4;
  }

  public void setItem_4(Integer item_4) {
    this.item_4 = item_4;
  }

  public Integer getItem_5() {
    return item_5;
  }

  public void setItem_5(Integer item_5) {
    this.item_5 = item_5;
  }

  public Integer getKills() {
    return kills;
  }

  public void setKills(Integer kills) {
    this.kills = kills;
  }

  public Integer getDeaths() {
    return deaths;
  }

  public void setDeaths(Integer deaths) {
    this.deaths = deaths;
  }

  public Integer getAssists() {
    return assists;
  }

  public void setAssists(Integer assists) {
    this.assists = assists;
  }

  public Integer getLeaver_status() {
    return leaver_status;
  }

  public void setLeaver_status(Integer leaver_status) {
    this.leaver_status = leaver_status;
  }

  public Integer getGold() {
    return gold;
  }

  public void setGold(Integer gold) {
    this.gold = gold;
  }

  public Integer getLast_hits() {
    return last_hits;
  }

  public void setLast_hits(Integer last_hits) {
    this.last_hits = last_hits;
  }

  public Integer getDenies() {
    return denies;
  }

  public void setDenies(Integer denies) {
    this.denies = denies;
  }

  public Integer getGold_per_min() {
    return gold_per_min;
  }

  public void setGold_per_min(Integer gold_per_min) {
    this.gold_per_min = gold_per_min;
  }

  public Integer getXp_per_min() {
    return xp_per_min;
  }

  public void setXp_per_min(Integer xp_per_min) {
    this.xp_per_min = xp_per_min;
  }

  public Integer getGold_spent() {
    return gold_spent;
  }

  public void setGold_spent(Integer gold_spent) {
    this.gold_spent = gold_spent;
  }

  public Integer getHero_damage() {
    return hero_damage;
  }

  public void setHero_damage(Integer hero_damage) {
    this.hero_damage = hero_damage;
  }

  public Integer getTower_damage() {
    return tower_damage;
  }

  public void setTower_damage(Integer tower_damage) {
    this.tower_damage = tower_damage;
  }

  public Integer getHero_healing() {
    return hero_healing;
  }

  public void setHero_healing(Integer hero_healing) {
    this.hero_healing = hero_healing;
  }

  public Integer getLevel() {
    return level;
  }

  public void setLevel(Integer level) {
    this.level = level;
  }

  public List<Ability_upgrade> getAbility_upgrades() {
    return ability_upgrades;
  }

  public void setAbility_upgrades(List<Ability_upgrade> ability_upgrades) {
    this.ability_upgrades = ability_upgrades;
  }
}
