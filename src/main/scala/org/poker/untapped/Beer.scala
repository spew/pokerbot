package org.poker.untapped

class CheckinBeer(val bid: Long, val beer_name: String, val beer_label: String, val beer_style: String, val beer_abv: Double, auth_rating: Int,
                   wish_list: Boolean, beer_active: Int) {

}

class Beer(val bid: Long, val beer_name: String, val beer_label: String, val beer_abv: Double, val beer_ibu: Int,
           val beer_description: String, val beer_style: String, val is_in_production: Int, val beer_slug: String,
           val created_at: String, val rating_count: Int, val rating_score: Double, stats: BeerStats, val brewery: Brewery) {

}
