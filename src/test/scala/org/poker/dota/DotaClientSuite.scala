package org.poker.dota

import org.scalatest.fixture
import org.poker.untapped.UntappedClient
import org.poker.steam._
import com.twitter.finagle.TimeoutException

class DotaClientSuite extends fixture.FunSuite {
  case class FixtureParam(client: SteamClient)

  private val channelPlayers = {
    new KnownPlayer(38926297L, List("whitey", "pete"), false)::
      new KnownPlayer(80342375L, List("bertkc", "brett", "bank", "gorby"), true)::
      new KnownPlayer(28308237L, List("mike"))::
      new KnownPlayer(10648475L, List("fud", "spew", "deathdealer69"))::
      new KnownPlayer(28326143L, List("steven", "bunk"))::
      new KnownPlayer(125412282L, List("mark", "clock", "cl0ck"))::
      new KnownPlayer(78932949L, List("muiy", "dank"))::
      new KnownPlayer(34117856L, List("viju", "vijal"))::
      new KnownPlayer(29508928L, List("sysm"))::
      Nil
  }

  def withFixture(test: OneArgTest) = {
    val steamClient = new SteamClient("")
    withFixture(test.toNoArgTest(FixtureParam(steamClient)))
  }

  test("get latest dota matches") { (f) =>
    val matchIds = new scala.collection.mutable.HashSet[Long]()
    for (player <- channelPlayers) {
      println(s"retrieving matches for player ${player.aliases.head}")
      var lastMatchId: Option[Long] = None
      do {
        try {
          val ids = f.client.getLatestDotaMatches(player.id, 25, lastMatchId).map(m => m.match_id)
          ids.foreach(id => matchIds.add(id))
          if (ids.isEmpty) lastMatchId = None else lastMatchId = Some(ids.last)
        } catch {
          case t: TimeoutException =>
        }
      } while (lastMatchId.isDefined)

    }
  }
}
