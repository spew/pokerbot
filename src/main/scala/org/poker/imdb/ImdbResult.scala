package org.poker.imdb

abstract class ImdbResult
case class FoundResult(val Title: String, val Year: String, val Rated: String, val Released: String,
                 val Runtime: String, val imdbRating: String, val Genre: String) extends ImdbResult
case class NotFoundResult(val Response: String, val Error: String) extends ImdbResult

