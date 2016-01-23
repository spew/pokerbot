package org.poker.imdb

import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JString, JObject, JField}

class ImdbResultSerializer extends CustomSerializer[ImdbResult](format => ({
  case JObject(List(JField("Title", JString(title)),
       JField("Year", JString(year)),
       JField("Rated", JString(rated)),
       JField("Released", JString(released)),
       JField("Runtime", JString(runtime)),
       JField("Genre", JString(genre)),
       JField("Director", JString(directory)),
       JField("Writer", JString(writer)),
       JField("Actors", JString(actors)),
       JField("Plot", JString(plot)),
       JField("Language", JString(language)),
       JField("Country", JString(country)),
       JField("Awards", JString(awards)),
       JField("Poster", JString(poster)),
       JField("Metascore", JString(metascore)),
       JField("imdbRating", JString(rating)),
       JField("imdbVotes", JString(votes)),
       JField("imdbID", JString(imdbId)),
       JField("Type", JString(filmType)),
       JField("Response", JString(response)))) => FoundResult(title, year, rated, released, runtime, rating, genre, imdbId)
  case JObject(List(JField("Response", JString(response)),
       JField("Error", JString(error)))) => NotFoundResult(response, error)
}, {
  case _ => JObject()
}))
