package org.poker.worldcup

import org.poker.util.JsonClient
import com.typesafe.scalalogging.slf4j.StrictLogging
import java.util.Date
import org.joda.time.DateTime

/**
 * Created by mylons on 6/17/14.
 */
class WorldCupClient extends JsonClient with StrictLogging {
  val baseUrl = "http://live.mobileapp.fifa.com/api/wc/matches"
  val headers = Nil

  def current = baseJson.group.filter( x => x.b_Current == true )

  def today = baseJson.group.filter( x => {
   val date = new DateTime(x.c_MatchDayDate)
   val today = DateTime.now()
   (date.dayOfMonth().get == today.dayOfMonth().get) &&
   (date.monthOfYear().get == today.monthOfYear().get)
  })

  def help = List()

  private def baseJson: GroupData = {
    val json = this.getJson("")
    (json \\ "data").extract[GroupData]
  }


}

case class MatchData(
                      b_Current: Boolean,
                      c_HomeTeam_en: String,
                      c_AwayTeam_en: String,
                      c_HomeNatioShort: String,
                      c_AwayNatioShort: String,
                      n_HomeGoals: Option[Int],
                      n_AwayGoals: Option[Int],
                      c_Phase_en: String,
                      c_Minute: String,
                      c_MatchStatusShort: String,
                      c_City: String,
                      b_Started: Boolean,
                      c_MatchDayDate: String
                      )
case class GroupData ( group: List[MatchData] )
/*
b_DateUnknown: Boolean,
c_StadiumImage: String,
c_HomeType: String,
c_Score: String,
c_Phase_de: String,
b_Current: Boolean,
c_MatchDayDate: String,
b_ShowMatchPhotos_de: Boolean,
n_AwayGoals: String,
b_ShowMatchPhotos_pt: Boolean,
c_MatchStatusShort: String,
b_DataEntryLiveScore: Boolean,
c_Date: String,
c_Competition_en: String,
c_HomeLogoImage: String,
c_Competition_es: String,
n_FifaEdition: Int,
c_HomeTeam_en: String,
b_Awarded: Boolean,
b_BlogAvailable_pt: Boolean,
n_MatchDay: Int,
b_Finished: Boolean,
c_MatchPosition: String,
c_BackgroundImage_de: String,
b_BlogAvailable_en: Boolean,
b_ShowMatchPhotos_es: Boolean,
b_ShowMatchPhotos_en: Boolean,
b_BlogAvailable_es: Boolean,
c_AwayType: String,
n_BlogID_de: Int,
c_FifaCompetition: String,
c_ShareURL_pt: String,
c_Stadium: String,
c_HomeTeam_fr: String,
c_HomeTeam_es: String,
c_Competition_pt: String,
b_ShowMatchPhotos_fr: Boolean,
c_Phase_es: String,
c_Phase_en: String,
b_BlogAvailable_fr: Boolean,
c_HomeNatioShort: String,
d_Date: Long,
n_HomeTeamID: Int,
c_AwayTeam_fr: String,
n_MatchNumber: Int,
b_Abandoned: Boolean,
n_AwayTeamID: Int,
c_Source: String,
n_BlogID_fr: Int,
b_Live: Boolean,
b_BlogOpen_es: Boolean,
c_HomeTeam_pt: String,
n_MatchID: Int,
c_Competition_fr: String,
c_AwayLogoImage: String,
b_BlogOpen_en: Boolean,
b_ShowComments: Boolean,
b_DataEntryLiveGoal: Boolean,
c_BackgroundImage_fr: String,
c_ShareURL_es: String,
c_AwayTeam_es: String,
n_CityID: Int,
n_CompetitionID: Int,
c_ShareURL_en: String,
c_AwayTeam_en: String,
b_MatchDetails: Boolean,
c_City: String,
n_BlogID_pt: Int,
b_Postponed: Boolean,
c_BackgroundImage_en: String,
b_BlogAvailable_de: Boolean,
c_CountryShort: String,
b_BlogOpen_de: Boolean,
d_MatchDayDate: Long,
c_ShareURL_fr: String,
b_MOTMWon: Boolean,
c_AwayTeam_de: String,
c_ShareURL: String,
c_HomeTeam_de: String,
b_Suspended: Boolean,
b_BlogOpen_pt: Boolean,
c_Competition_de: String,
b_Lineup: Boolean,
"c_CompetitionType": "national",
"c_Phase_fr": "Groupe A",
"n_HomeGoals": null,
"c_AwayTeam_pt": "Cro\u00e1cia",
"c_Minute": null,
"b_RescheduledToBeResumed": false,
"b_MOTMCanVote": false,
"c_AwayNatioShort": "CRO",
"b_BlogOpen_fr": false,
"n_BlogID_es": 2474,
"c_ShareURL_de": "http://de.fifa.com/worldcup/matches/round=255931/match=300186453/index.html#nosticky",
"c_BackgroundImage": "http://www.fifa.com/mm/photo/tournament/competition/02/37/17/31/2371731_xxlarge-lnd.jpg",
"c_BackgroundImage_pt": "http://pt.fifa.com/mm/photo/tournament/competition/02/37/17/31/2371731_xxlarge-lnd.jpg",
"b_Started": false,
"n_BlogID_en": 2470,
"b_TimeUnknown": false,
"c_BackgroundImage_es": "http://es.fifa.com/mm/photo/tournament/competition/02/37/17/31/2371731_xxlarge-lnd.jpg",
"c_Phase_pt": "Grupo A",
"n_WeatherCode": 0,
"n_StadiumID": 5007289
*/
