package org.poker.poller

import java.util.concurrent.{Executors, ScheduledFuture, TimeUnit}

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.poker.util.DaemonThreadFactory
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent.Reason

class DiscordBotReconnectPoller(discordClient: IDiscordClient) extends Poller with LazyLogging {
  discordClient.getDispatcher.registerListener(this)
  val executor = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory())
  val runnable = buildRunnable()
  var future: Option[ScheduledFuture[_]] = None

  @EventSubscriber
  def onDisconnectedEvent(event: DiscordDisconnectedEvent): Unit = {
    logger.warn("Disconnected from discord: {}", event.getReason)
    if (shouldReconnect(event.getReason)) {
      logger.info("Reconnecting...")
      discordClient.login()
    }
  }

  def shouldReconnect(reason : Reason) = {
    reason match {
      case DiscordDisconnectedEvent.Reason.UNKNOWN => true
      case DiscordDisconnectedEvent.Reason.MISSED_PINGS => true
      case DiscordDisconnectedEvent.Reason.TIMEOUT => true
      case _ => false
    }
  }

  override def start(): Unit = {
    val f = executor.scheduleAtFixedRate(runnable, 15, 30, TimeUnit.SECONDS)
    future = Some(f)
  }

  override def stop(): Unit = {
    future match {
      case Some(f) => f.cancel(true)
      case None =>
    }
  }

  def buildRunnable() : Runnable = {
    new Runnable {
      override def run(): Unit = {
        if (!discordClient.isReady) {
          logger.warn("DiscordClient is not ready, reconnecting...")
          discordClient.login()
        }
      }
    }
  }
}
