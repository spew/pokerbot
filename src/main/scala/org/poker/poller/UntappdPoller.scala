package org.poker.poller

import java.util.concurrent.{Future, TimeUnit, Executors}

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.pircbotx.PircBotX
import org.poker.ProgramConfiguration
import org.poker.untapped.{UntappdMessageFormatter, Checkin, UntappedClient}
import org.poker.util.DaemonThreadFactory
import scala.collection.JavaConversions._

class UntappdPoller(configuration: ProgramConfiguration, ircBot: PircBotX) extends Poller with LazyLogging {
  val untappedClient = new UntappedClient(configuration.untappdClientId.get, configuration.untappdClientSecret.get, configuration.untappdAccessToken.get)
  val executor = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory())
  val ignoredUsers = Set("Rayvl2001", "milnak", "Tommyhawkers", "Bigshooter83" )
  var future: Option[Future[_]] = None

  override def start(): Unit = {
    var lastMaxId: Option[Long] = None
    val runnable = new Runnable {
      override def run(): Unit = {
        try {
          val response = untappedClient.recentFriendCheckins()
          val checkins = response.response.checkins.items
          lastMaxId match {
            case Some(maxId) => {
              val filteredCheckins = checkins.filter(k => k.checkin_id > maxId && !ignoredUsers.contains(k.user.user_name))
              if (!filteredCheckins.isEmpty) {
                val newMax = filteredCheckins.map(c => c.checkin_id).max
                lastMaxId = Some(newMax)
              }
              for (c <- filteredCheckins) {
                val beerInfo = untappedClient.beerInfo(c.beer.bid)
                val message = UntappdMessageFormatter.formatCheckin(c, beerInfo.response.beer)
                for (channel <- ircBot.getUserBot.getChannels) {
                  channel.send.message(message)
                }
              }
            }
            case None => {
              if (!response.response.checkins.items.isEmpty) {
                lastMaxId = Some(checkins.head.checkin_id)
              }
            }
          }
        } catch {
          case e: Exception => {
            logger.error("problem getting latest untappd friends", e)
          }
        }
      }
    }
    val f = executor.scheduleAtFixedRate(runnable, 15, 120, TimeUnit.SECONDS)
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
