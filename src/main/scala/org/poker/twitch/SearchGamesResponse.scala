package org.poker.twitch

class SearchGamesResponse(val games: List[Game]) {

}

class Game(val name: String, val popularity: Int, val _id: Long) {

}