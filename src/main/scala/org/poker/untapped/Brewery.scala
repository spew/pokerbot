package org.poker.untapped

class Brewery(val brewery_id: Long, val brewery_name: String, val brewery_label: String, val country_name: String,
              val contact: BreweryContact, val location: BreweryLocation) {

}

class BreweryContact(val twitter: Option[String], val facebook: Option[String], val instagram: Option[String], val url: String) {

}

class BreweryLocation(val brewery_city: String, val brewery_state: String, val lat: Double, val lng: Double) {

}