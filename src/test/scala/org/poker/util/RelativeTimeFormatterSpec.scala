package org.poker.util

import com.github.nscala_time.time.Imports._
import org.poker.test.UnitSpec

class RelativeTimeFormatterSpec extends UnitSpec {
  "two months" should "succeed" in {
    val from = new DateTime(2014, 2, 20, 0, 0, 0)
    val to = new DateTime(2014, 4, 20, 0, 0, 0)
    val relativeTime = RelativeTimeFormatter.relativeToDate(from, to)
    assert(relativeTime == "2 months from now")
  }

  "two years, 3 months, and two days" should "succeed" in {
    val from = new DateTime(2012, 1, 20, 0, 0, 0)
    val to = new DateTime(2014, 4, 22, 0, 0, 0)
    val relativeTime = RelativeTimeFormatter.relativeToDate(from, to)
    assert(relativeTime == "2 years from now")
  }
}
