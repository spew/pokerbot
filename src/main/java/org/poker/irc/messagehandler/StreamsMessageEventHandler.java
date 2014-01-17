package org.poker.irc.messagehandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.Configuration;
import org.poker.irc.MessageEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class StreamsMessageEventHandler implements MessageEventHandler {
  private static final Logger LOG = LoggerFactory.getLogger(StreamsMessageEventHandler.class);
  private final Configuration configuration;

  public StreamsMessageEventHandler(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public String getDescription() {
    return "!streams or .streams: returns a list of twitch streams.";
  }

  @Override
  public String[] getMessagePrefixes() {
    return new String[] { "!twitch", ".twitch", "!streams", ".streams" };
  }

  @Override
  public void onMessage(MessageEvent event) {
    Gson gson = new GsonBuilder().create();
    String json;
    HttpGet httpGet = new HttpGet("https://api.twitch.tv/kraken/search/streams?q=dota2");
    httpGet.addHeader("Client-ID", this.configuration.getTwitchClientId());
    httpGet.addHeader("Accept", "application/vnd.twitchtv.v2+json");
    try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(httpGet)) {
      HttpEntity httpEntity = response.getEntity();
      try (InputStream inputStream = httpEntity.getContent()) {
        String myString = IOUtils.toString(inputStream, "UTF-8");
        LOG.info(myString);

        // InputStreamReader json = gson.fromJson(reader, String.class);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
