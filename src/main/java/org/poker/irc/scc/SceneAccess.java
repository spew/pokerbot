package org.poker.irc.scc;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class SceneAccess {
  private static final String SCENEACCESS_URL = "https://sceneaccess.eu";
  private final URL baseUrl;
  private final Credentials credentials;
  private Map<String, String> cookies = Maps.newHashMap();

  public SceneAccess(Credentials credentials) {
    this.credentials = credentials;
    try {
      this.baseUrl = new URL(SCENEACCESS_URL);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public String getUrl() {
    return SCENEACCESS_URL;
  }

  private void login(Response response) {
    try {
      Response loginResponse = Jsoup.connect(response.url().toString())
          .data("username", credentials.getUsername())
          .data("password", credentials.getPassword())
          .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
          .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.45 Safari/537.36")
          .followRedirects(true)
          .cookies(response.cookies())
          .method(Connection.Method.POST)
          .execute();
      this.cookies = loginResponse.cookies();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Torrent> findShow(String showName) {
    Document document;
    try {
      Response searchResponse = Jsoup.connect(this.baseUrl.toString() + "/browse?search=" + showName + "&method=2&c27=27")
          .cookies(cookies)
          .method(Method.GET)
          .execute();
      if (!searchResponse.url().toString().toLowerCase().contains("browse?search")) {
        this.login(searchResponse);
        return this.findShow(showName);
      }
      document = searchResponse.parse();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Elements rowElements = document.select("tr.tt_row");
    List<Torrent> torrents = Lists.newArrayList();
    for (Element elem : rowElements) {
      final Element aDetails = elem.select("td.ttr_name").first().select("a").first();
      String detailsUrl = this.getUrl() + "/" + aDetails.attr("href");
      Document detailsDocument;
      try {
        detailsDocument = Jsoup.connect(detailsUrl)
            .cookies(cookies)
            .get();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      final Element titleElement = detailsDocument.select("span.fls").first();
      Torrent t = new Torrent() {
        {
          setTitle(titleElement.text());
          setUrl(aDetails.attr("href"));
        }
      };
      torrents.add(t);
      break;
    }
    return torrents;
  }
}
