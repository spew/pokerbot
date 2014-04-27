package org.poker.irc;

import com.google.api.client.util.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.javatuples.Pair;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

public class HttpUtils {
  // needed to trust all https sites without importing their certificates, obv bad but who really cares for a bot
  private static TrustManager[] getAllCertsTrustManager() {
    return new TrustManager[]{
        new X509TrustManager() {
          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
          }
          public void checkClientTrusted( java.security.cert.X509Certificate[] certs, String authType ) {	}
          public void checkServerTrusted( java.security.cert.X509Certificate[] certs, String authType ) {	}
        }
    };
  }

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
    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
    try {
      SSLContext sc = SSLContext.getInstance("SSL");
      TrustManager[] trustAllCerts = HttpUtils.getAllCertsTrustManager();
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      httpClientBuilder.setSslcontext(sc);
      httpClientBuilder.setHostnameVerifier(
          new X509HostnameVerifier() {
            @Override
            public boolean verify(String urlHostName, SSLSession session) {
              return true;
            }

            @Override
            public void verify(String host, SSLSocket ssl) throws IOException {
            }

            @Override
            public void verify(String host, X509Certificate cert) throws SSLException {
            }

            @Override
            public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
            }
          });
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      throw new RuntimeException(e);
    }
    try (CloseableHttpClient httpClient = httpClientBuilder.build();
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
