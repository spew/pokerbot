package org.poker.worldcup

import org.scalatest.fixture

class WorldCupClientSuite extends fixture.FunSuite {
  case class FixtureParam(client: WorldCupClient)

  def withFixture(test: OneArgTest) = {
    val untappedClient = new WorldCupClient()
    withFixture(test.toNoArgTest(FixtureParam(untappedClient)))
  }

  test("match results should deserialize") { (f) =>
    f.client.getMatches()
  }

  test("today's matches should deserialize") { (f) =>
    f.client.getTodaysMatches()
  }

  test("tomorrow's matches should deserialize") { (f) =>
    f.client.getTomorrowsMatches()
  }
}
