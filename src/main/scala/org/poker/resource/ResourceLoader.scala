package org.poker.resource

import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods
import resource._

import scala.io.Source

object ResourceLoader {
  implicit val formats = DefaultFormats

  def extractToString(path: String): String = {
    for (source <- managed(Source.fromURL(getClass.getResource(path)))) {
      return source.mkString
    }
    null
  }

  /*def jsonToObject[A <: AnyRef](path: String): A = {
    //for (stream <- managed(getClass.getResourceAsStream(path))) {
    (managed(getClass.getResourceAsStream(path)) map { stream =>
      val json = JsonMethods.parse(stream)
      val mf = scala.reflect.Manifest.singleType[A](_)
      json.extract(format, mf)
    }).opt.get
  }*/
}
