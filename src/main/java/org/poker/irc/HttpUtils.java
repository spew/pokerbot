package org.poker.irc;

import com.google.api.client.util.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.javatuples.Pair;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

public class HttpUtils {
  public static <T> T getJson(String url, Class<T> classOfT) {
    return HttpUtils.getJson(url, classOfT, null);
  }

  public static <T> T getJson(String url, Class<T> classOfT, Pair<String, String> ... headers) {
    try {
      return HttpUtils.getJson(new URI(url), classOfT, headers);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T getJson(URI url, Class<T> classOfT, Pair<String, String> ... headers) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    T result;
    HttpGet httpGet = new HttpGet(url);
    boolean hasAccept = false;
    if (headers != null) {
      for (Pair<String, String> pair : headers) {
        httpGet.addHeader(pair.getValue0(), pair.getValue1());
        if (pair.getValue0().equals("Accept")) {
          hasAccept = true;
        }
      }
    }
    if (!hasAccept) {
      httpGet.addHeader("Accept", "application/json");
    }
    try (CloseableHttpClient httpClient = HttpClients.createDefault();
         CloseableHttpResponse response = httpClient.execute(httpGet)) {
      HttpEntity httpEntity = response.getEntity();
      try (JsonReader reader = new JsonReader(new InputStreamReader(httpEntity.getContent()))) {
        reader.setLenient(true);
        result = gson.fromJson(reader, classOfT);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return result;
  }
}
