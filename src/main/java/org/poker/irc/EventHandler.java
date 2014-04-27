package org.poker.irc;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventHandler extends ListenerAdapter {
  private static class RejoinChannelAttempt {
    public KickEvent kickEvent;
    public boolean joined;
  }
  private static final Logger LOG = LoggerFactory.getLogger(EventHandler.class);
  private Map<String, MessageEventHandler> messageEventHandlerMap = Maps.newHashMap();
  private Map<Pattern, MessageEventHandler> regexEventHandlerMap = Maps.newHashMap();
  private List<MessageEventHandler> messageEventHandlers = Lists.newArrayList();
  private Object lock = new Object();
  private List<RejoinChannelAttempt> rejoiningChannels = Lists.newArrayList();
  private final Configuration configuration;

  public EventHandler(Configuration configuration) {
    this.configuration = configuration;
  }

  public void addMessageEventHandler(final MessageEventHandler messageEventHandler) {
    if (messageEventHandler.getMessagePrefixes() != null) {
      for (String prefix : messageEventHandler.getMessagePrefixes()) {
        messageEventHandlerMap.put(prefix, messageEventHandler);
      }
    }
    if (messageEventHandler.getMessageRegex() != null) {
      Pattern pattern = Pattern.compile(messageEventHandler.getMessageRegex());
      regexEventHandlerMap.put(pattern, messageEventHandler);
    }
    messageEventHandlers.add(messageEventHandler);
  }

  @Override
  public void onConnect(ConnectEvent event) {
    if (this.configuration.getPerformActions().isEmpty()) {
      LOG.info("No perform command set.");
    } else {
      LOG.info("Sending perform command");
      for (String action : this.configuration.getPerformActions()) {
        event.getBot().sendRaw().rawLine(action);
      }
    }
  }

  @Override
  public void onJoin(final JoinEvent event) {
    synchronized (this.rejoiningChannels) {
      for (int i = this.rejoiningChannels.size() - 1; i >= 0; i--) {
        RejoinChannelAttempt attempt = this.rejoiningChannels.get(i);
        if (attempt.kickEvent.getChannel().compareTo(event.getChannel()) == 0) {
          if (attempt.kickEvent.getTimestamp() <= event.getTimestamp()) {
            synchronized (attempt) {
              attempt.joined = true;
              attempt.notify();
              this.rejoiningChannels.remove(i);
            }
          }
        }
      }
    }
  }

  @Override
  public void onKick(final KickEvent event) throws Exception {
    if (event.getRecipient().compareTo(event.getBot().getUserBot()) == 0) {
      LOG.warn("Kicked from {} by {}: {}", event.getChannel(), event.getUser(), event.getReason());
      // TODO: does this block all event handling? if so we should thread this off
      RejoinChannelAttempt rejoinChannelAttempt = new RejoinChannelAttempt();
      rejoinChannelAttempt.kickEvent = event;
      synchronized (this.rejoiningChannels) {
        this.rejoiningChannels.add(rejoinChannelAttempt);
      }
      synchronized (rejoinChannelAttempt) {
        while (!rejoinChannelAttempt.joined) {
          event.getBot().sendIRC().joinChannel(event.getChannel().getName());
          rejoinChannelAttempt.wait(1000);
        }
      }
    }
  }

  @Override
  public void onMessage(final MessageEvent event) throws Exception {
    String message = event.getMessage();
    User user = event.getUser();
    if(message.equalsIgnoreCase(".help") || message.equalsIgnoreCase("!help")){
      for(MessageEventHandler handler : messageEventHandlers){
        user.send().message(handler.getDescription());
      }
    } else{
      for (Map.Entry<String, MessageEventHandler> entry : messageEventHandlerMap.entrySet()) {
        if (message.startsWith(entry.getKey())) {
          try {
            entry.getValue().onMessage(event);
          } catch (Throwable t) {
            LOG.error("Error in handler", t);
          }
        }
      }
      for (Map.Entry<Pattern, MessageEventHandler> entry : regexEventHandlerMap.entrySet()) {
        Matcher matcher = entry.getKey().matcher(message);
        if (matcher.matches()) {
          try {
            entry.getValue().onMessage(event);
          } catch (Throwable t) {
            LOG.error("Error in handler", t);
          }
        }
      }
    }
  }
}
