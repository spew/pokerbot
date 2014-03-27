package org.poker.irc.messagehandler;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.MessageEventHandler;

import java.io.IOException;

public class BeerMessageEventHandler implements MessageEventHandler {
  @Override
  public String[] getMessagePrefixes() {
    return new String[] { "!beer", ".beer" };
  }

  @Override
  public String getDescription() {
    return "Use !beer, .beer <beer name> to find your favorite brew";
  }

  @Override
  public void onMessage(MessageEvent event) {
    String message = event.getMessage();
    String beerName = null;
    for (String s : this.getMessagePrefixes()) {
      if (message.startsWith(s)) {
        beerName = message.substring(s.length()).trim();
      }
    }
    if (Strings.isNullOrEmpty(beerName)) {
      return;
    }
    Document document;
    try {
      document = Jsoup.connect(String.format("http://beeradvocate.com/search/?q=%s&qt=beer", beerName)).get();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    //document.select()
  }
}
