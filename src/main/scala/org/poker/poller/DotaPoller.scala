package org.poker.poller

import java.util.concurrent.{TimeUnit, Future, Executors}

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.pircbotx.PircBotX
import org.poker.ProgramConfiguration
import org.poker.dota.{DotaMatchFormatter, LatestMatchFinder}
import org.poker.steam.SteamClient
import org.poker.util.DaemonThreadFactory
import scala.collection.JavaConversions._

class DotaPoller(val pc: ProgramConfiguration, ircBot: PircBotX) extends Poller with LazyLogging {
  val executor = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory())
  val steamClient = new SteamClient(pc.steamApiKey.getOrElse(""))
  val latestMatchFinder = new LatestMatchFinder(steamClient)
  var future: Option[Future[_]] = None
  private var lastMatchId: Option[Long] = None
  private val runnable = new Runnable {
    override def run(): Unit = {
      try {
        val latestMatch = latestMatchFinder.findLatestMatch()
        lastMatchId match {
          case Some(id) => {
            if (id != latestMatch.match_id) {
              val message = DotaMatchFormatter.format(latestMatch)
              for (channel <- ircBot.getUserBot.getChannels) {
                channel.send.message(message)
              }
              lastMatchId = Some(latestMatch.match_id)
            }
          }
          case None => {
            lastMatchId = Some(latestMatch.match_id)
          }
        }
      } catch {
        case e: Exception => {
          logger.error("Problem retrieving latest dota results", e)
        }
      }
    }
  }

  override def start(): Unit = {
    val f = executor.scheduleAtFixedRate(runnable, 5, 60, TimeUnit.SECONDS)
    future = Some(f)
  }

  override def stop(): Unit = {
    future match {
      case Some(f) => {
        f.cancel(true)
      }
      case None => {

      }
    }
    executor.shutdownNow()
  }
}
