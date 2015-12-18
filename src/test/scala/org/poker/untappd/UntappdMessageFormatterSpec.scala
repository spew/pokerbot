package org.poker.untappd

import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods
import org.poker.resource.ResourceLoader
import org.poker.test.UnitSpec
import org.poker.untapped.{UntappedResponse, BeerInfoResult, CheckinsResponse, UntappdMessageFormatter}
import org.scalatest.Matchers

class UntappdMessageFormatterSpec extends UnitSpec with Matchers {
  implicit val formats = DefaultFormats
  val recentCheckinsResponse =  JsonMethods.parse(ResourceLoader.extractToString("/untappd/get-recent-checkins-response.json")).extract[UntappedResponse[CheckinsResponse]]
  val beerInfoResponse =  JsonMethods.parse(ResourceLoader.extractToString("/untappd/get-beer-info.json")).extract[UntappedResponse[BeerInfoResult]]

  "formatCheckin" should "match" in {
    val message = UntappdMessageFormatter.formatCheckin(recentCheckinsResponse.response.checkins.items.head, beerInfoResponse.response.beer)
    message should startWith ("M4ttj0nes rated 'Focal Banger (The Alchemist)' 4.5/5.0 (avg 4.7)")
    message should endWith ("http://untappd.com/user/M4ttj0nes/checkin/172733802")
  }

  "formatBeer" should "match" in {
    val message = UntappdMessageFormatter.formatBeer(beerInfoResponse.response.beer)
    assert(message == "Heady Topper | 4.7/5.0 | style: Imperial / Double IPA | abv: 8.0 | ibu: 100 | https://untappd.com/b/the-alchemist-heady-topper/4691")
  }
}
