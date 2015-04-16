package org.poker.imdb

import org.json4s.NoTypeHints
import org.json4s.native.{JsonMethods, Serialization}
import org.poker.resource.ResourceLoader
import org.poker.test.UnitSpec

class ImdbMessageFormatterSpec extends UnitSpec {
  implicit val formats = Serialization.formats(NoTypeHints) + new ImdbResultSerializer
  val searchResponse = JsonMethods.parse(ResourceLoader.extractToString("/imdb/search-response.json")).extract[ImdbResult]
  "search result formatting" should "match" in {
    val message = ImdbMessageFormatter.format(searchResponse)
    assert(message == "How the West Was Won | Rating: 7.1 | Genre: Western |  Rated: APPROVED | Released: 20 Feb 1963")
  }

  "not found formatting" should "match" in {
    val message = ImdbMessageFormatter.format(new NotFoundResult("Resonse", "Error"))
    assert(message == "Film not found.")
  }
}
