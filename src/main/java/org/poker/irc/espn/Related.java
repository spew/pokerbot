
package org.poker.irc.espn;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("com.googlecode.jsonschema2pojo")
public class Related {

    @Expose
    private Integer id;
    @Expose
    private String title;
    @Expose
    private Links links;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

}
