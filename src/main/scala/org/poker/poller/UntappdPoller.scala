package org.poker.poller

import java.util.concurrent.{TimeUnit, Executors}

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.pircbotx.PircBotX
import org.poker.ProgramConfiguration
import org.poker.untapped.{Checkin, UntappedClient}
import org.poker.util.DaemonThreadFactory
import scala.collection.JavaConversions._

class UntappdPoller(configuration: ProgramConfiguration, ircBot: PircBotX) extends Poller with LazyLogging {
  val untappedClient = new UntappedClient(configuration.untappdClientId.get, configuration.untappdClientSecret.get, configuration.untappdAccessToken.get)
  val executor = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory())
  override def start(): Unit = {
    var lastMaxId: Option[Long] = None
    val runnable = new Runnable {
      override def run(): Unit = {
        try {
          val response = untappedClient.recentFriendCheckins()
          val checkins = response.response.checkins.items
          lastMaxId match {
            case Some(maxId) => {
              val filteredCheckins = checkins.filter(k => k.checkin_id > maxId)
              if (!filteredCheckins.isEmpty) {
                val newMax = filteredCheckins.map(c => c.checkin_id).max
                lastMaxId = Some(newMax)
              }
              for (c <- filteredCheckins) {
                val message = formatCheckinMessage(c)
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
    executor.scheduleAtFixedRate(runnable, 10, 60, TimeUnit.SECONDS)
  }

  override def stop(): Unit = {

  }

  private def formatCheckinMessage(checkin: Checkin) = {
    val beerInfo = untappedClient.beerInfo(checkin.beer.bid)
    val rating = formatRating(beerInfo.response.beer.rating_score)
    val url = s"http://untappd.com/user/${checkin.user.user_name}/checkin/${checkin.checkin_id}"
    val venueMessage = if (checkin.venue.isDefined) s"at '${checkin.venue.get.venue_name}' " else ""
    s"${checkin.user.user_name} just rated '${checkin.beer.beer_name}' ${checkin.rating_score}/5 (avg ${rating}) ${venueMessage}| ${url}"
  }

  private def formatRating(rating: Double) = {
    f"${rating}%1.1f"
  }
}
