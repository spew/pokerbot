package org.poker.irc.messagehandler;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.CustomsearchRequestInitializer;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.MessageEventHandler;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class GoogleSearchMessageEventHandler implements MessageEventHandler {
  private Customsearch customsearch;

  public GoogleSearchMessageEventHandler() {
    this.customsearch = this.createClient();
  }

  @Override
  public String[] getMessagePrefixes() {
    return new String[]{".google", "!google"};
  }

  @Override
  public void onMessage(MessageEvent event) {
    String message = event.getMessage();
    String query;
    if (message.startsWith(".google")) {
      query = message.substring(".google".length()).trim();
    } else {
      query = message.substring("!google".length()).trim();
    }

    String API_KEY = System.getenv().get("SEARCH_API_KEY");
    String CX_KEY = System.getenv().get("SEARCH_CX_KEY");

    if (Strings.isNullOrEmpty(API_KEY)) {
      event.getChannel().send().message("Can't google: set the API_KEY environment variable");
      return;
    }
    if (Strings.isNullOrEmpty(CX_KEY)) {
      event.getChannel().send().message("Can't google: set the SEARCH_CX_KEY environment variable");
      return;
    }

    try {
      Customsearch.Cse.List list = this.customsearch.cse().list(query);
      list.setCx(CX_KEY);
      list.setKey(API_KEY);

      Search result = list.execute();
      List<Result> listResult = result.getItems();

      if (listResult == null || listResult.isEmpty() ) {
        event.getChannel().send().message("Nothing found.");
      } else {
        Result first = listResult.get(0);
        String link = first.getLink();
        String title = first.getTitle();

        event.getChannel().send().message(title);
        event.getChannel().send().message(link);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Customsearch createClient() {
    try {
      JsonFactory JSON_FACTORY = new JacksonFactory();
      HttpTransport httpTransport;
      HttpRequestInitializer httpRequestInitializer = new HttpRequestInitializer() {
        @Override
        public void initialize(HttpRequest httpRequest) throws IOException {
        }
      };
      Customsearch.Builder client;
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();

      client = new Customsearch.Builder(httpTransport, JSON_FACTORY, httpRequestInitializer).setApplicationName("#poker");
      Customsearch newClient = client.build();
      return newClient;
    } catch (IOException | GeneralSecurityException e) {
      throw new RuntimeException(e);
    }
  }
}