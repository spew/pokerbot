package org.poker.doge

class DogecoinAverageResponse(val currency_name: String, val currency_code: String, val markets: List[Market], val ts: String, val date: String, val vwap: BigDecimal) {
}

class Market(val price: BigDecimal, val volume: Long, val ts: String, val market_url: String, val exchange_name: String) {

}