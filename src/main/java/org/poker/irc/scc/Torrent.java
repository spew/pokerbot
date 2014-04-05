package org.poker.irc.scc;

import org.joda.time.DateTime;

public class Torrent {
  private String title;
  private String url;
  private DateTime dateAdded;

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public DateTime getDateAdded() {
    return dateAdded;
  }

  public void setDateAdded(DateTime dateAdded) {
    this.dateAdded = dateAdded;
  }
}
