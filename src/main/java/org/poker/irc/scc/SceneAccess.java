package org.poker.irc.scc;

import java.net.MalformedURLException;
import java.net.URL;

public class SceneAccess {
  private static final String SCENEACCESS_URL = "https://sceneaccess.eu/";
  private final URL baseUrl;
  public SceneAccess() {
    try {
      this.baseUrl = new URL(SCENEACCESS_URL);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public void findShow() {

  }
}
