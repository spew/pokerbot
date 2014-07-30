package org.poker.util

import org.json4s._
import com.stackmob.newman._
import java.net.{URL}
import com.stackmob.newman.dsl._
import org.json4s.native.JsonMethods._
import scala.concurrent._
import scala.concurrent.duration._
import com.stackmob.newman.request.HttpRequest
import com.stackmob.newman.Constants._
import com.stackmob.newman.response.HttpResponseCode

trait JsonClient {
  implicit val formats = DefaultFormats ++ org.json4s.ext.JodaTimeSerializers.all
  implicit val httpClient = new ApacheHttpClient
  val baseUrl: String
  val headers: List[(String, String)]

  protected def getJson(relativeUri: String): JValue = {
    val httpRequest = getRequest(relativeUri)
    val httpResponse = Await.result(httpRequest.apply, 3.second)
    if (httpResponse.code != HttpResponseCode.Ok) {
      throw new Exception(s"Bad response code (${httpResponse.code}) from '${httpRequest.url}'")
    }
    parse(httpResponse.bodyString(UTF8Charset))
  }

  private def getRequest(relativeUrl: String): HttpRequest = {
    val url = new URL(baseUrl + relativeUrl)
    SimpleRetrier.retry(4)(GET(url).addHeaders(headers))
  }

}
