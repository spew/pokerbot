package org.poker.untapped

class CheckinsResponse(val checkins: CheckinsList) {

}

class CheckinsList(val count: Int, val items: List[Checkin]) {

}

class Checkin(val checkin_id: Long, val created_at: String, val checkin_comment: String, val rating_score: Double, val user: User, val beer: CheckinBeer, val venue: Option[Venue]) {

}

class User(val uid: Long, val user_name: String, val first_name: String, val last_name: String, val location: String, val url: String,
            val is_supporter: Int, val relationship: String, val bio: String, val user_avatar: String) {

}

class Venue(val venue_id: Long, val venue_name: String, val primary_category: String, val parent_category_id: String, val contact: VenueContact) {

}

class VenueContact(val twitter: String, val venue_url: String) {

}