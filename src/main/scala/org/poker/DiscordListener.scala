package org.poker

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.poker.handler.{ExpectedFailureException, MessageEventHandler}
import sx.blah.discord.api.EventSubscriber
import sx.blah.discord.handle.impl.events.{MessageReceivedEvent, ReadyEvent}


class DiscordListener extends LazyLogging {
  var handlers = List[MessageEventHandler]()

  def addHandler(handler: MessageEventHandler): Unit = {
    handlers = handler::handlers
  }

  @EventSubscriber
  def onReady(readyEvent: ReadyEvent): Unit = {

  }

  @EventSubscriber
  def onMessageReceived(messageReceivedEvent: MessageReceivedEvent): Unit = {
    for (h <- handlers) {
      val m = h.messageMatchRegex.findFirstMatchIn(messageReceivedEvent.getMessage.getContent)
      if (m.isDefined) {
        try {
          h.onMessage(messageReceivedEvent, m.get)
        } catch {
          case e: ExpectedFailureException => {

          }
          case e: org.jsoup.HttpStatusException => {
            logger.warn("Error executing org.poker.handler", e)
            messageReceivedEvent.getMessage.getChannel.sendMessage(e.getMessage() + s": ${e.getStatusCode}")
          }
          case t: Throwable => {
            logger.warn("Error executing org.poker.handler", t)
            val message = formatExceptionMessage(t.getMessage)
            messageReceivedEvent.getMessage.getChannel.sendMessage(message)
          }
        }
      }
    }
  }

  private def formatExceptionMessage(message: String): String = {
    if (message.contains("\n")) {
      message.substring(0, message.indexOf("\n")) + "..."
    } else {
      message
    }
  }
}
