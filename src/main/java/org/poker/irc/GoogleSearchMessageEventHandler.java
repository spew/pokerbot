package org.poker.irc;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.CustomsearchRequestInitializer;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleSearchMessageEventHandler implements MessageEventHandler {

    @Override
    public String[] getMessagePrefixes(){
        return new String[] {".google", "!google"};
    }


    @Override
    public void onMessage(MessageEvent event){
        String message = event.getMessage();
        String query;
        if (message.startsWith(".rt")){
            query = message.substring(".google".length()).trim();
        }else {
            query = message.substring("!google".length()).trim();
        }
        //set these keys in your IDE/ENV
        String API_KEY =  System.getenv().get("SEARCH_API_KEY");
        String CX_KEY = System.getenv().get("SEARCH_CX_KEY");

        JsonFactory JSON_FACTORY = new JacksonFactory();
        HttpTransport httpTransport;
        HttpRequestInitializer httpRequestInitializer = new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) throws IOException {
            }
        };

        try{
            Customsearch.Builder client;
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            client = new Customsearch.Builder(httpTransport,JSON_FACTORY, httpRequestInitializer).setApplicationName("#poker");
            Customsearch newClient = client.build();
            Customsearch.Cse.List list = newClient.cse().list(query);
            list.setCx(CX_KEY);
            list.setKey(API_KEY);

            Search result = list.execute();
            List<Result> listResult =  result.getItems();

            if (listResult.isEmpty()) {
                event.getChannel().send().message("Nothing found.");
            }else {
                Result first =  listResult.get(0);
                String link = first.getLink();
                String title = first.getTitle();

                event.getChannel().send().message(title);
                event.getChannel().send().message(link);
            }

        } catch (IOException e){
            System.out.println(e.getMessage());
        } catch (Throwable t){
            t.printStackTrace();
        }

    }
}