package org.poker.irc;

import com.google.common.collect.Maps;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EventHandler extends ListenerAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(EventHandler.class);
  private Map<String, MessageEventHandler> messageEventHandlerMap = Maps.newHashMap();
  public void addMessageEventHandler(final MessageEventHandler messageEventHandler) {
    for (String prefix : messageEventHandler.getMessagePrefixes()) {
      this.messageEventHandlerMap.put(prefix, messageEventHandler);
    }
  }

  @Override
  public void onMessage(final MessageEvent event) throws Exception {
    String message = event.getMessage();
    for (Map.Entry<String, MessageEventHandler> entry : messageEventHandlerMap.entrySet()) {
      if (message.startsWith(entry.getKey())) {
        try {
          entry.getValue().onMessage(event);
        } catch (Throwable t) {
          LOG.error("Error in handler", t);
        }
      }
    }
  }
}
