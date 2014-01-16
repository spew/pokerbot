package org.poker.irc;

import com.google.common.collect.Maps;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.Map;

public class EventHandler extends ListenerAdapter {
  private Map<String, MessageEventHandler> messageEventHandlerMap = Maps.newHashMap();
  public void addMessageEventHandler(final MessageEventHandler messageEventHandler) {
    for (String prefix : messageEventHandler.getMessagePrefixes()) {
      this.messageEventHandlerMap.put(prefix + " ", messageEventHandler);
    }
  }

  @Override
  public void onMessage(final MessageEvent event) throws Exception {
    String message = event.getMessage();
    for (Map.Entry<String, MessageEventHandler> entry : messageEventHandlerMap.entrySet()) {
      if (message.startsWith(entry.getKey())) {
        entry.getValue().onMessage(event);
      }
    }
  }
}
