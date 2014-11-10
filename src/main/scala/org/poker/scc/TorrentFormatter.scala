package org.poker.scc

import org.joda.time.format.{PeriodFormatterBuilder, PeriodFormatter}
import com.github.nscala_time.time.Imports._
import org.ocpsoft.prettytime.PrettyTime

class TorrentFormatter {
  def format(torrent: SceneTorrent, sceneAccessClient: SceneAccessClient): String = {
    val url = sceneAccessClient.url + "/" + torrent.url
    val period = (torrent.dateAdded to DateTime.now).toPeriod
    val duration = period.toDurationFrom(new DateTime())
    val seconds = duration.toStandardSeconds.getSeconds.toLong
    val prettyTime = new PrettyTime(new java.util.Date(seconds * 1000))
    //val prettyTime = new PrettyTime(new java.util.Date(period.toStandardDuration.getStandardSeconds * 1000))
    val formattedDate = prettyTime.format(new java.util.Date(0))
    s"${torrent.title} | ${formattedDate} | 720p | ${url}"
  }
}
