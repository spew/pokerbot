package org.poker.imdb

import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.poker.util.JsonClient

class ImdbClient extends JsonClient {
  // http://www.omdbapi.com/?t=The+Insider&y=&plot=short&r=json
  override implicit val formats = Serialization.formats(NoTypeHints) + new ImdbResultSerializer
  val baseUrl = "http://www.omdbapi.com"
  val headers = Nil

  def getFilmInfo(filmTitle: String) : ImdbResult = {
    //val url = baseUrl + s"/t?=${filmTitle}&y=&plot=short&r=json"
    val urlEncodedTitle = filmTitle.replaceAll(" ", "+")
    val url =  s"/?t=${urlEncodedTitle}&y=&plot=short&r=json"
    val json = getJson(url)
    json.extract[ImdbResult]
  }

}
