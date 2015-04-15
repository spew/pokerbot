package org.poker.util

import java.text.DecimalFormat

object HumanReadableLargeNumberFormatter {
  val suffixes = Seq("", "k", "m", "b", "t")
  val exponentRegex = "E[0-9]".r
  val formatter = new DecimalFormat("##0E0")

  def format(value: BigDecimal): String = {
    val f = formatter.format(value)
    val replacement = suffixes(f(f.size - 1).asDigit / 3)
    exponentRegex.replaceAllIn(f, replacement)
  }
}
