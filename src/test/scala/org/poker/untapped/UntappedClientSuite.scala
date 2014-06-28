package org.poker.untapped

import org.scalatest.fixture

class UntappedClientSuite extends fixture.FunSuite {
  case class FixtureParam(client: UntappedClient)

  def withFixture(test: OneArgTest) = {
    val untappedClient = new UntappedClient("clientId", "clientSecret")
    withFixture(test.toNoArgTest(FixtureParam(untappedClient)))
  }

  test("search results should deserialize") { (f) =>
    f.client.beerSearch("Heady Topper")
  }

  test("info results should deserialize") { (f) =>
    f.client.beerInfo(4691L)
  }
}