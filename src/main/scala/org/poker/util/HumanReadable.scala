package org.poker.util

import java.text.DecimalFormat

trait HumanReadable extends BigDecimal { self =>
  val suffixes = Seq("", "k", "m", "b", "t")
  val exponentRegex = "E[0-9]".r
  val formatter = new DecimalFormat("##0E0")

  def toStringHumanReadable(): String = {
    val f = formatter.format(self)
    val replacement = suffixes(f(f.size - 1).asDigit / 3)
    exponentRegex.replaceAllIn(f, replacement)
  }
}