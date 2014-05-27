package org.poker.untapped
import com.github.nscala_time.time.Imports.DateTime

// TODO: convert created_at to DateTime
class BeerSummary(val bid: Long, val beer_name: String, val beer_label: String, val beer_abv: Double, val beer_ibu: Double, val beer_style: String, val beer_description: String, val created_at: String) {

}
