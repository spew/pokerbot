package org.poker.util

import org.poker.test.UnitSpec

class HumanReadableLargeNumberFormatterSpec extends UnitSpec {
  "1024" should "return 1k" in {
    val value = HumanReadableLargeNumberFormatter.format(BigDecimal(1024))
    assert(value == "1.02k")
  }

  "1000000" should "return 1m" in {
    val value = HumanReadableLargeNumberFormatter.format(BigDecimal(1000000))
    assert(value == "1m")
  }
}
