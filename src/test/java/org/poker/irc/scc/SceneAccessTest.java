package org.poker.irc.scc;

import org.junit.Before;
import org.junit.Test;

public class SceneAccessTest {
  SceneAccess sceneAccess;
  @Before
  public void setup() {
    this.sceneAccess = new SceneAccess(new Credentials(null, null));
  }

  @Test
  public void findShowTest() {
    this.sceneAccess.findShow("true detective");
  }
}
