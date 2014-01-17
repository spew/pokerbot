
package org.poker.irc.espn;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("com.googlecode.jsonschema2pojo")
public class Category {

    @Expose
    private String description;
    @Expose
    private String type;
    @Expose
    private Integer sportId;
    @Expose
    private Integer leagueId;
    @Expose
    private League league;
    @Expose
    private String uid;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSportId() {
        return sportId;
    }

    public void setSportId(Integer sportId) {
        this.sportId = sportId;
    }

    public Integer getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Integer leagueId) {
        this.leagueId = leagueId;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
