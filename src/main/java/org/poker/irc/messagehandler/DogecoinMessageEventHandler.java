package org.poker.irc.messagehandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.MessageEventHandler;
import java.io.IOException;

public class DogecoinMessageEventHandler implements MessageEventHandler {

    @Override
    public String getDescription() {
        return "!doge or .doge: gives you the latest info on DogeCoin";
    }

    @Override
    public String[] getMessagePrefixes() {
        return new String[] { ".doge", "!doge" };
    }

    @Override
    public void onMessage(MessageEvent event) {

        String url = "http://www.coinwarz.com/cryptocurrency/coins/dogecoin";
        Document document;

        try {
            document = Jsoup.connect(url).get();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("DOGE/BTC - Cryptsy: ");
        sb.append(document.select("td a[href*=cryptsy] b").text());
        sb.append(" | Vircurex: ");
        sb.append(document.select("td a[href*=vircurex] b").text());
        sb.append(" | CoinEx: ");
        sb.append(document.select("td a[href*=coinex] b").text());
        //sb.append(" | w.avg: ");
        //sb.append(ticker.get());
        event.getChannel().send().message(sb.toString());
    }
}
