package org.poker.twitch

class StreamsResponse(val _links : Links, val streams: List[org.poker.twitch.Stream]) {

}

class Links(val self: String, val featured: Option[String], val summary: Option[String], val followed: Option[String], val next: Option[String]) {

}

class Stream(val name: String, val broadcaster: String, val _id: Long, val game: String, val viewers: Long, val _links: Links, val channel: Channel) {

}

class Channel(val mature: Option[Boolean], val status: String, val display_name: String, val game: String, val _id: Long,
               val name: String, val url: String, val _links: Links) {

}