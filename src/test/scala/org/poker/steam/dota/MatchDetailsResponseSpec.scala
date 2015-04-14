package org.poker.steam.dota

import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods
import org.poker.test.UnitSpec

import resource._

class MatchDetailsResponseSpec extends UnitSpec {
  implicit val formats = DefaultFormats

  "deserialize match history response" should "succeed" in {
    for (stream <- managed(getClass.getResourceAsStream("/get-match-history-response.json"))) {
      val matchHistory = JsonMethods.parse(stream).extract[MatchHistoryResponse]
      assert(!matchHistory.matches.isEmpty)
      assert(10 == matchHistory.matches(0).players.size)
    }
  }
}
