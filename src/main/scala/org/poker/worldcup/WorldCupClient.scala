package org.poker.worldcup

import org.poker.util.JsonClient
import com.typesafe.scalalogging.slf4j.StrictLogging
import com.github.nscala_time.time.Imports._
import org.json4s.{DefaultFormats}

class WorldCupClient extends JsonClient with StrictLogging {
  val baseUrl = "http://worldcup.sfg.io/"
  object WorldCupFormats extends DefaultFormats {
    val losslessDate = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
    override val dateFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
  }
  implicit override val formats = WorldCupFormats ++ org.json4s.ext.JodaTimeSerializers.all
  val headers = Nil

  def getTodaysMatches() = {
    getMatches(Some("today"))
  }

  def getTomorrowsMatches() = {
    getMatches(Some("tomorrow"))
  }

  def getMatches(): List[Match] = {
    getMatches(None)
  }

  private def getMatches(dayIdentifier: Option[String]) = {
    val json = this.getJson(s"/matches/${dayIdentifier.getOrElse("")}")
    json.extract[List[Match]]
  }
}

case class Match(match_number: Long,
                 location: String,
                 datetime: DateTime,
                 status: String,
                 home_team: Team,
                 away_team: Team,
                 winner: String)

case class Team(country: String, code: String, goals: Option[Int])