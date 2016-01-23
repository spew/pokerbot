package org.poker.imdb

object ImdbMessageFormatter {
  def format(imdbResult: ImdbResult): String = {
    imdbResult match {
      case FoundResult(title, year, parentalRating, released, runtime, rating, genre, imdbId) =>
        s"${title} | Rating: ${rating} | Genre: ${genre} |  Rated: ${parentalRating} | Released: ${released} | http://www.imdb.com/title/${imdbId}"
      case NotFoundResult(response, error) =>
        s"Film not found."
    }
  }
}
