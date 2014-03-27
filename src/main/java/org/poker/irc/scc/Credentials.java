package org.poker.irc.scc;

public class Credentials {
  private final String password;
  private final String username;

  public Credentials(String username, String password)  {
    this.username = username;
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  public String getUsername() {
    return username;
  }
}
