package org.poker.irc.messagehandler;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import it.jtomato.JTomato;
import it.jtomato.gson.Movie;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.MessageEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RottenTomatoesMessageEventHandler implements MessageEventHandler {
  private static final Logger LOG = LoggerFactory.getLogger(RottenTomatoesMessageEventHandler.class);
  @Override
  public String[] getMessagePrefixes() {
    return new String[] {".rt", "!rt"};
  }

  @Override
  public String getDescription() {
    return "!rt <movie name> or .rt <movie name>: returns the critics rating and audience rating for a film.";
  }

  @Override
  public void onMessage(MessageEvent event) {
    String message = event.getMessage();
    String movieName;

    if (message.startsWith(".rt")) {
      movieName = message.substring(".rt".length()).trim();
    } else {
      movieName = message.substring("!rt".length()).trim();
    }

    Map<String, String> env = System.getenv();
    String API_KEY = env.get("RT_API_KEY");

    if (Strings.isNullOrEmpty(API_KEY)) {
      event.getChannel().send().message("Can't RottenTomato: set the RT_API_KEY environment variable");
    } else {
      JTomato jTomato = new JTomato(API_KEY);
      List<Movie> movies = new ArrayList<Movie>();
      int total = jTomato.searchMovie(movieName, movies, 1);
      if (total == 0) {
          event.getChannel().send().message("Movie '" + movieName + "' not found. Incorrect movie name?");
      } else {
          Movie movie = movies.get(0);
          event.getChannel().send().message("Name: " + movie.title + "  Audience Rating: " + movie.rating.audienceScore +
                                                "%  Critics Rating: " + movie.rating.criticsScore + "%");
      }
    }
  }
}
