package org.poker.irc.messagehandler;

import com.google.common.collect.Maps;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.MessageEventHandler;

import java.io.IOException;
import java.util.Map;

public class DotabuffMessageEventHandler implements MessageEventHandler {
  private Map<String, String> nameToId = Maps.newHashMap();

  public DotabuffMessageEventHandler() {
    this.populateDefaultPlayers();
  }

  @Override
  public String[] getMessagePrefixes() {
    return new String[] { "!dota ", "!dotabuff ", ".dota ", ".dotabuff "};
  }

  @Override
  public String getDescription() {
    return "!dotabuff <name> or .dotabuff <name> : gives you the wins, losses and dotabuff url for that player.";
  }

  @Override
  public void onMessage(MessageEvent event) {
    String message = event.getMessage();
    if (message.startsWith("!dotabuff")) {
      message = message.substring("!dotabuff".length()).trim();
    } else if (message.startsWith(".dotabuff")) {
      message = message.substring(".dotabuff".length()).trim();
    } else if (message.startsWith("!dota")) {
      message = message.substring("!dota".length()).trim();
    } else {
      message = message.substring(".dota".length()).trim();
    }
    message = message.toLowerCase();
    if (this.nameToId.containsKey(message)) {
      String playerId = this.nameToId.get(message);
      String url = "http://dotabuff.com/players/" + playerId;
      Document document;
      try {
        document = Jsoup.connect(url).get();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      Element wonSpan = document.select("span.won").first();
      String gamesWon = wonSpan.text();
      Element lostSpan = document.select("span.lost").first();
      String gamesLost = lostSpan.text();
      event.getChannel().send().message(url);
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("wins: ");
      stringBuilder.append(gamesWon);
      stringBuilder.append((" | "));
      stringBuilder.append("losses: ");
      stringBuilder.append(gamesLost);
      event.getChannel().send().message(stringBuilder.toString());
    } else {
      event.getChannel().send().message("Unknown player name: " + message);
    }
  }

  private void populateDefaultPlayers() {
    this.nameToId.put("whitey", "38926297");
    this.nameToId.put("pete", "38926297");
    this.nameToId.put("bertkc", "80342375");
    this.nameToId.put("brett", "80342375");
    this.nameToId.put("bank", "80342375");
    this.nameToId.put("mike", "28308237");
    this.nameToId.put("fud", "10648475");
    this.nameToId.put("spew", "10648475");
    this.nameToId.put("deathdealer69", "10648475");
    this.nameToId.put("steven", "28326143");
    this.nameToId.put("clock", "125412282");
    this.nameToId.put("cl0ck", "125412282");
    this.nameToId.put("muiy", "78932949");
    this.nameToId.put("dank", "78932949");
  }
}
