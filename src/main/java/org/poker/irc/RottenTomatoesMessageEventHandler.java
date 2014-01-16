package org.poker.irc;

import it.jtomato.JTomato;
import it.jtomato.gson.Movie;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: brett
 * Date: 1/16/14
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class RottenTomatoesMessageEventHandler implements MessageEventHandler {
    @Override
    public String[] getMessagePrefixes(){
        return new String[] {".rt", "!rt"};
    }

    @Override
    public void onMessage(MessageEvent event){
        String message = event.getMessage();
        String movieName;
        if (message.startsWith(".rt")){
            movieName = message.substring(".rt".length()).trim();
        }else {
            movieName = message.substring("!rt".length()).trim();
        }

        Map<String, String> env = System.getenv();
        String API_KEY = env.get("RT_API_KEY");
        JTomato jTomato = new JTomato(API_KEY);
        List<Movie> movies = new ArrayList<Movie>();
        int total = jTomato.searchMovie(movieName,movies,1);
        if(total == 0){
            event.getChannel().send().message("Movie not found. Bad movie name?");
        }else{
            Movie movie = movies.get(0);
            event.getChannel().send().message("Name: " + movie.title + "  Audience Rating: " + movie.rating.audienceScore + "%  Critics Rating: " + movie.rating.criticsScore + "%" );
        }

    }
}
