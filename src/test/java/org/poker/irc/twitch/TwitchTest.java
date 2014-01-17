package org.poker.irc.twitch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class TwitchTest {
  private static final Logger logger = LoggerFactory.getLogger(TwitchTest.class);

  @Before
  public void setup() {
  }

  @Test
  public void testConstructor() throws IOException {
    URL url = this.getClass().getResource("/streams.json");
    File jsonFile = new File(url.getFile());
    Gson gson = new GsonBuilder().create();
    try (InputStreamReader reader = new InputStreamReader(new FileInputStream(jsonFile))) {
      gson.fromJson(reader, StreamsResponse.class);
    }
  }
}
