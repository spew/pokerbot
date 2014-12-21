package org.poker.poller

import java.util.concurrent.{TimeUnit, Executors}

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.pircbotx.{Channel, PircBotX}
import org.poker.ProgramConfiguration
import org.poker.dota.KnownPlayers
import org.poker.steam.SteamClient
import org.poker.util.{RelativeTimeFormatter, DaemonThreadFactory}
import com.github.nscala_time.time.Imports._

class BunkPoller(configuration: ProgramConfiguration, ircBot: PircBotX) extends Poller with LazyLogging {
  val executor = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory())
  val xmasDay = new DateTime(2014, 12, 25, 0, 0)
  private var lastDate: Option[DateTime] = None;
  private val runnable = new Runnable {
    override def run(): Unit = {
      lastDate match {
        case Some(d) => {
          val now = DateTime.now
          val array = new Array[Channel](ircBot.getUserBot.getChannels.size())
          for (c <- ircBot.getUserBot.getChannels.toArray(array)) {
            if (now.isBefore(xmasDay)) {
              val hoursUntil = (now to xmasDay).toDuration.toStandardHours
              if (hoursUntil.getHours <= 1 || now.getHourOfDay != d.getHourOfDay) {
                val relativeTimeMsg = RelativeTimeFormatter.relativeToDate(now, xmasDay)
                c.send.message(s"bunk returns to dota about ${relativeTimeMsg}")
              }
            } else {
              if (now.getHourOfDay != d.getHourOfDay) {
                val steamClient = new SteamClient(configuration.steamApiKey.get)
                val latestMatch = steamClient.getLatestDotaMatches(KnownPlayers.steven.id, 1)
                val startTime = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeZone.UTC).plusSeconds(latestMatch.head.start_time.toInt)
                if (startTime.isBefore(xmasDay)) {
                  val hours = (xmasDay to now).toDuration.toStandardHours.getHours
                  val s = if (hours == 1) "" else "s"
                  c.send.message(s"bunk's return to dota is in violation about ${hours} hour${s}")
                } else {

                }
              }

            }
          }
        }
        case None => {
        }
      }
      lastDate = Some(DateTime.now)
    }
  }

  override def start {
    val f = executor.scheduleAtFixedRate(runnable, 1, 20, TimeUnit.SECONDS)
  }

  override def stop {
  }
}
