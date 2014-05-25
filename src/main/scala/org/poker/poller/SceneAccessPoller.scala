package org.poker.poller

import org.poker.ProgramConfiguration
import com.typesafe.scalalogging.slf4j.LazyLogging
import akka.actor.{Cancellable, Actor, Props}
import scala.concurrent.duration._
import scala.collection.mutable.Map

import org.poker.scc.SceneShow
import org.joda.time.DateTimeConstants


class SceneAccessPoller(configuration: ProgramConfiguration) extends Poller with LazyLogging with Actor {
  val system = akka.actor.ActorSystem("system")
  import system.dispatcher
  var dispatchCancellable: Option[Cancellable] = None
  val showNameToDispatch = Map[SceneShow, (Cancellable, FiniteDuration)]()
  val sceneShowActor = system.actorOf(Props(classOf[SceneShowActor], new SceneShowActor()))
  val shows = this.createShows()

  override def start(): Unit = {
    dispatchCancellable match {
      case (Some(c)) => throw new Exception("Can't start poller more than once")
      case None => Unit
    }
    logger.debug("starting scc polling")
    val actor = system.actorOf(Props(classOf[SceneAccessPoller], this))
    dispatchCancellable = Option(system.scheduler.schedule(10 seconds, 1 minute, actor, None))
  }

  override def stop(): Unit = {
    dispatchCancellable match {
      case (Some(c)) => c.cancel()
      case None => Unit
    }
  }

  override def receive = {
    case None => {
      var delay = 0
      for (s <- shows) {
        if (showNameToDispatch.contains(s)) {

        } else {
          val interval = s.currentPollWait()
          system.scheduler.schedule(delay seconds, interval, )
        }
      }
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

class SceneShowActor extends Actor with LazyLogging {
  override def receive = {
    case None => {

    }
  }
}
