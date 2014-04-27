package org.poker.irc.messagehandler;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.Sets;
import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.ocpsoft.prettytime.PrettyTime;
import org.pircbotx.hooks.events.MessageEvent;
import org.poker.irc.Configuration;
import org.poker.irc.MessageEventHandler;
import org.poker.irc.steam.*;
import org.slf4j.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DotabuffMessageEventHandler implements MessageEventHandler {
  private static final Logger LOG = LoggerFactory.getLogger(DotabuffMessageEventHandler.class);
  ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
  private Map<String, Integer> nameToId = Maps.newHashMap();
  private Dota dota;
  private enum MatchResult {
    WIN("won"), LOSS("lost");
    private String outputValue;
    private MatchResult(String outputValue) {
      this.outputValue = outputValue;
    }
  }
  private MatchResult streakType;

  public DotabuffMessageEventHandler(Configuration configuration) {
    this.populateDefaultPlayers();
    this.dota = new Dota(configuration);
  }

  @Override
  public String getMessageRegex() {
    return null;
  }

  @Override
  public String[] getMessagePrefixes() {
    return new String[] { "!dota", ".dota" };
  }

  @Override
  public String getDescription() {
    return "!dotabuff <player> or .dotabuff <player> : send to channel the wins, losses, current streak, and dotabuff URL for <player>";
  }

  @Override
  public void onMessage(MessageEvent event) {
    String message = event.getMessage();
    if (message.startsWith("!dotabuff")) {
      message = message.substring("!dotabuff".length()).trim();
    } else if (message.startsWith(".dotabuff")) {
      message = message.substring(".dotabuff".length()).trim();
    } else if (message.startsWith("!dota")) {
      message = message.substring("!dota".length()).trim();
    } else {
      message = message.substring(".dota".length()).trim();
    }
    message = message.toLowerCase();
    if (this.nameToId.containsKey(message)) {
      final Integer playerId = this.nameToId.get(message);
      String url = "http://dotabuff.com/players/" + playerId;
      Document document;
      try {
        document = Jsoup.connect(url).get();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      int currentStreak = 0;
      Stopwatch stopwatch = Stopwatch.createStarted();
      List<MatchDetails> recentResults = getRecentResults(playerId, 10);
      stopwatch.stop();
      LOG.info("Time to fetch match details: {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
      int streakWins = 0, streakLosses = 0;
      Iterable<MatchResult> matchResults = Iterables.transform(recentResults, new Function<MatchDetails, MatchResult>() {
        @Override
        public MatchResult apply(final MatchDetails matchDetails) {
          return DotabuffMessageEventHandler.this.checkMatchResult(matchDetails, playerId);
        }
      });
      MatchResult prev = Iterables.getFirst(matchResults, null);
      for (MatchResult result : matchResults) {
        if (result.equals(prev)) {
          currentStreak++;
        } else {
          break;
        }
      }
      for (MatchResult result : matchResults) {
        switch (result) {
          case WIN:
            streakWins++;
            break;
          case LOSS:
            streakLosses++;
            break;
          default:
            throw new NotImplementedException();
        }
        prev = result;
      }
      MatchResult streakType = Iterables.getFirst(matchResults, null);
      Element wonSpan = document.select("span.won").first();
      String gamesWon = wonSpan.text();
      Element lostSpan = document.select("span.lost").first();
      String gamesLost = lostSpan.text();
      event.getChannel().send().message(url);
      StringBuilder sb = new StringBuilder();
      sb.append("wins: ");
      sb.append(gamesWon);
      sb.append(" | ");
      sb.append("losses: ");
      sb.append(gamesLost);
      sb.append(" | streak: ");
      sb.append(streakType.outputValue);
      sb.append(" ");
      sb.append(currentStreak);
      sb.append(" | last ten: ");
      sb.append(streakWins);
      sb.append("-");
      sb.append(streakLosses);
      sb.append(" | last played: ");
      MatchDetails firstMatch = Iterables.getFirst(recentResults, null);
      DateTime dateTime = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeZone.UTC).plusSeconds(firstMatch.getStart_time());
      Period period = new Period(dateTime, DateTime.now(DateTimeZone.UTC));
      PrettyTime prettyTime = new PrettyTime(new Date(period.toStandardDuration().getStandardSeconds() * 1000));
      sb.append(prettyTime.format(new Date(0)));
      event.getChannel().send().message(sb.toString());
    } else {
      if (Strings.isNullOrEmpty(message.trim())) {
        Match latestMatch = this.findLastPlayed();
        event.getChannel().send().message("Latest match: " + "http://dotabuff.com/matches/" + latestMatch.getMatch_id());
      } else {
        event.getChannel().send().message("Unknown player name: " + message);
      }
    }
  }

  private Match findLastPlayed() {
    Set<Integer> playerIds = Sets.newHashSet();
    playerIds.addAll(this.nameToId.values());
    playerIds.remove(86745912);
    Match latestMatch = null;
    for (Integer id : playerIds) {
      List<Match> matches = this.dota.getMatches(id, 1);
      Match match = Iterables.getFirst(matches, null);
      if (match == null) {
        continue;
      }
      if (latestMatch == null) {
        latestMatch = match;
      }
      if (latestMatch.getStart_time() < match.getStart_time()) {
        latestMatch = match;
      }
    }
    try {
      Thread.sleep(250);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return latestMatch;
  }

  private List<MatchDetails> getRecentResults(long playerId, int maxResults) {
    List<Match> recentMatches = this.dota.getMatches(playerId, 2 * maxResults);
    Queue<ListenableFuture<MatchDetails>> futures = Queues.newArrayDeque();
    List<MatchDetails> results = Lists.newArrayList();
    int curIdx;
    for (curIdx = 0 ; curIdx < recentMatches.size() && curIdx < maxResults; curIdx++) {
      final Match match = recentMatches.get(curIdx);
      futures.add(this.submitGetMatchDetailsRequest(match));
    }
    while (futures.size() > 0) {
      ListenableFuture<MatchDetails> f = futures.remove();
      MatchDetails matchDetails;
      try {
        matchDetails = f.get();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
      if (this.isMatchValid(matchDetails)) {
        results.add(matchDetails);
      } else {
        if (curIdx < recentMatches.size()) {
          final Match match = recentMatches.get(curIdx);
          futures.add(this.submitGetMatchDetailsRequest(match));
          curIdx++;
        }
      }
    }
    return results;
  }

  private ListenableFuture<MatchDetails> submitGetMatchDetailsRequest(final Match match) {
    ListenableFuture<MatchDetails> listenableFuture = this.executorService.submit(new Callable<MatchDetails>() {
      @Override
      public MatchDetails call() throws Exception {
        return DotabuffMessageEventHandler.this.dota.getMatchDetails(match.getMatch_id());
      }
    });
    return listenableFuture;
  }

  private boolean isMatchValid(MatchDetails matchDetails) {
    for (Player player : matchDetails.getPlayers()) {
      /* NULL - player is a bot.
          2 - player abandoned game.
          1 - player left game after the game has become safe to leave.
          0 - Player stayed for the entire match.*/
      if (player.getLeaver_status() == null) {
        return false;
      }
      if (player.getLeaver_status() == 2) {
        if (matchDetails.getDuration() < 10 * 60) {
          return false;
        }
      }
    }
    return true;
  }

  private MatchResult checkMatchResult(MatchDetails matchDetails, long playerId) {
    boolean radiantWin = matchDetails.getRadiant_win();
    for (Player player : matchDetails.getPlayers()) {
      if (player.getAccount_id() == playerId) {
        if (player.getPlayer_slot() < 128) {
          return radiantWin ? MatchResult.WIN : MatchResult.LOSS;
        } else {
          return radiantWin ? MatchResult.LOSS : MatchResult.WIN;
        }
      }
    }
    throw new RuntimeException("Player id not found: " + playerId);
  }

  private void populateDefaultPlayers() {
    this.nameToId.put("whitey", 38926297);
    this.nameToId.put("pete", 38926297);
    this.nameToId.put("bertkc", 80342375);
    this.nameToId.put("brett", 80342375);
    this.nameToId.put("bank", 80342375);
    this.nameToId.put("mike", 28308237);
    this.nameToId.put("fud", 10648475);
    this.nameToId.put("spew", 10648475);
    this.nameToId.put("deathdealer69", 10648475);
    this.nameToId.put("steven", 28326143);
    this.nameToId.put("bunk", 28326143);
    this.nameToId.put("clock", 125412282);
    this.nameToId.put("cl0ck", 125412282);
    this.nameToId.put("muiy", 78932949);
    this.nameToId.put("dank", 78932949);
    this.nameToId.put("viju", 34117856);
    this.nameToId.put("vijal", 34117856);
    this.nameToId.put("sysm", 29508928);
    this.nameToId.put("rtz", 86745912);
    this.nameToId.put("arteezy", 86745912);
  }
}
