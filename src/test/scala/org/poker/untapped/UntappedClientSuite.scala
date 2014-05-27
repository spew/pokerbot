package org.poker.untapped

import org.scalatest.fixture

class UntappedClientSuite extends fixture.FunSuite {
  case class FixtureParam(client: UntappedClient)

  def withFixture(test: OneArgTest) = {
    val untappedClient = new UntappedClient("B1F8749410D45CEB0251E08149E83E33801A7402", "D433CA2F08F0EE2C0FF093ACCB5FD6638928583F")
    withFixture(test.toNoArgTest(FixtureParam(untappedClient)))
  }

  test("search results should deserialize") { (f) =>
    f.client.beerSearch("Heady Topper")
  }

  test("info results should deserialize") { (f) =>
    f.client.beerInfo(4691L)
  }
}