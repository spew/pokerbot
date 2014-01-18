package org.poker.irc.steam;

import com.google.gson.annotations.*;

public class Ability_upgrade {
  @Expose
  private Integer ability;
  @Expose
  private Integer time;
  @Expose
  private Integer level;

  public Integer getAbility() {
    return ability;
  }

  public void setAbility(Integer ability) {
    this.ability = ability;
  }

  public Integer getTime() {
    return time;
  }

  public void setTime(Integer time) {
    this.time = time;
  }

  public Integer getLevel() {
    return level;
  }

  public void setLevel(Integer level) {
    this.level = level;
  }
}
