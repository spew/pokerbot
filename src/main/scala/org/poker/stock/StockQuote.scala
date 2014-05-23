package org.poker.stock

class StockQuote(t: String, l_fix : Double, c_fix: Double, cp_fix: Double, el_fix: Option[Double], ec_fix: Option[Double], ecp_fix: Option[Double]) {
  val symbol = t
  val currentPriceUsd = l_fix.toDouble
  val currentPriceDifferenceUsd = c_fix
  val currentDifferencePercentage = cp_fix
  val extraHoursPriceUsd = el_fix
  val extraHoursPriceDifferenceUsd = ec_fix
  val extraHoursCurrentPriceDifferencePercentage = ecp_fix
}
