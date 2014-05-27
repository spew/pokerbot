package org.poker.poller

import org.poker.ProgramConfiguration
import com.typesafe.scalalogging.slf4j.LazyLogging
import akka.actor.{ActorRef, Cancellable, Actor, Props}
import scala.concurrent.duration._
import scala.collection.concurrent.{TrieMap, Map}

import com.github.nscala_time.time.Imports.DateTime
import org.poker.scc.{TorrentFormatter, SceneTorrent, SceneAccessClient, SceneShow}
import org.joda.time.{DateTimeZone, Period, DateTimeConstants}
import org.pircbotx.{Channel, PircBotX}


class SceneAccessPoller(configuration: ProgramConfiguration, ircBot: PircBotX) extends Poller with LazyLogging {
  val system = akka.actor.ActorSystem("system")
  import system.dispatcher
  var dispatchCancellable: Option[Cancellable] = None
  val sceneAccessClient = new SceneAccessClient(configuration)
  val shows = this.createShows()

  override def start(): Unit = {
    dispatchCancellable match {
      case (Some(c)) => throw new Exception("Can't start poller more than once")
      case None => Unit
    }
    logger.debug("starting scc polling")
    val actor = system.actorOf(Props(classOf[Manager], shows, ircBot, sceneAccessClient))
    dispatchCancellable = Option(system.scheduler.schedule(10 seconds, 1 minute, actor, "schedule"))
  }

  override def stop(): Unit = {
    dispatchCancellable match {
      case (Some(c)) => c.cancel()
      case None => Unit
    }
  }

  private def createShows(): Seq[SceneShow] = {
    val RealTime = new SceneShow("Real Time", DateTimeConstants.SATURDAY, 6)
    val TheAmericans = new SceneShow("The Americans", DateTimeConstants.THURSDAY, 3)
    val MadMen = new SceneShow("Mad Men", DateTimeConstants.MONDAY, 3)
    val SiliconValley = new SceneShow("Silicon Valley", DateTimeConstants.MONDAY, 3)
    val GameOfThrones = new SceneShow("Game of Thrones", DateTimeConstants.MONDAY, 2)
    val Veep = new SceneShow("Veep", DateTimeConstants.MONDAY, 3)
    Seq(RealTime, GameOfThrones, MadMen, SiliconValley, TheAmericans, Veep)
  }
}

class Manager(shows: Seq[SceneShow], ircBot: PircBotX, sceneAccessClient: SceneAccessClient) extends Actor with LazyLogging {
  val showToDispatch = new TrieMap[SceneShow, (Cancellable, FiniteDuration)]()
  val system = akka.actor.ActorSystem("system")
  val showToActor = this.createActors()
  import system.dispatcher

  override def receive = {
    case "schedule" => {
      var delay = 0
      for (s <- shows) {
        val currentInterval = s.currentPollWait()
        if (showToDispatch.contains(s)) {
          val dispatchTuple = showToDispatch.get(s)
          if (dispatchTuple.get._2 != currentInterval) {
            dispatchTuple.get._1.cancel()
            scheduleShowActor(s, delay, currentInterval)
            delay += 15
          }
        } else {
          scheduleShowActor(s, delay, currentInterval)
          delay += 15
        }
      }
    }
  }

  private def scheduleShowActor(show: SceneShow, delay: Int, currentInterval: FiniteDuration): Unit = {
    logger.debug(s"scheduling '${show.name}' polling with interval ${currentInterval}")
    val actor = showToActor.get(show).get
    val cancellable = system.scheduler.schedule(delay seconds, currentInterval, actor, "poll")
    val tuple = (cancellable, currentInterval)
    showToDispatch += show -> tuple
  }

  private def createActors(): scala.collection.immutable.Map[SceneShow, ActorRef] = {
    shows.map(s => (s, system.actorOf(Props(classOf[SceneShowActor], s, ircBot, sceneAccessClient)))).toMap
  }
}

class SceneShowActor(show: SceneShow, ircBot: PircBotX, sceneAccessClient: SceneAccessClient) extends Actor with LazyLogging {
  var lastDate: Option[DateTime] = None

  override def receive = {
    case "poll" => {
      logger.debug(s"waiting for scene-access client to check on latest version of '${show.name}'")
      val possibleTorrent = sceneAccessClient.synchronized {
        logger.debug(s"looking for most recent version of '${show.name}'")
        val torrents = sceneAccessClient.findShow(show.name)
        if (torrents.isEmpty) {
          None
        } else {
          Some(torrents.head)
        }
      }
      if (possibleTorrent.isDefined) {
        val torrent = possibleTorrent.get
        if (lastDate.isDefined && lastDate.get == torrent.dateAdded) {
          logger.debug(s"skipping result for '${show.name}' time is equal to the last time for show: ${torrent.dateAdded}'")
        } else {
          if (lastDate.isDefined && lastDate.get != torrent.dateAdded) {
            logger.info(s"updating last time for show ${show.name} from ${lastDate.get} to ${torrent.dateAdded}")
          }
          lastDate = Some(torrent.dateAdded)
          if (shouldPrint(torrent.dateAdded)) {
            sendNewShowMessage(torrent)
          } else {
            val now = new DateTime(DateTimeZone.UTC)
            logger.debug(s"not using torrent for '${show.name}', too old: ${torrent.dateAdded}, current time: ${now}")
          }
        }
      } else {
        logger.debug(s"nothing found for '${show.name}")
      }
    }
  }

  private def shouldPrint(dateAdded: DateTime): Boolean = {
    val duration = getDuration(dateAdded)
    val interval = 2.hours
    duration < interval
  }

  private def getDuration(dateAdded: DateTime): FiniteDuration = {
    val now = new DateTime(DateTimeZone.UTC)
    if (dateAdded.isBefore(now)) {
      val duration = new Period(dateAdded, now).toStandardDuration
      duration.toStandardSeconds.getSeconds.seconds
    } else {
      val duration = new Period(now, dateAdded).toStandardDuration
      duration.toStandardSeconds.getSeconds.seconds
    }
  }

  private def sendNewShowMessage(torrent: SceneTorrent): Unit = {
    val array = new Array[Channel](ircBot.getUserBot.getChannels.size())
    for (c <- ircBot.getUserBot.getChannels.toArray(array)) {
      val formatter = new TorrentFormatter()
      val message = formatter.format(torrent, sceneAccessClient)
      c.send.message(s"NEW release: " + message)
    }
  }
}
