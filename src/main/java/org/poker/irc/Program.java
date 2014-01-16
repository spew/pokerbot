package org.poker.irc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Program {
  private static final Logger LOG = LoggerFactory.getLogger(Program.class);
  public static void main(String[] args) {
    try {
      BotRunner botRunner = new BotRunner();
      botRunner.run();
    } catch (Exception e) {
      LOG.error("Uncaught exception, ending program", e);
    }
  }
}
