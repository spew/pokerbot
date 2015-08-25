package org.poker

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.poker.handler.{ExpectedFailureException, MessageEventHandler}
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.hooks.ListenerAdapter
import org.pircbotx.PircBotX

class BotListener() extends ListenerAdapter[PircBotX] with LazyLogging {
  var handlers = List[MessageEventHandler]()

  def addHandler(handler: MessageEventHandler): Unit = {
    handlers = handler::handlers
  }

  private def formatExceptionMessage(message: String): String = {
    if (message.contains("\n")) {
      message.substring(0, message.indexOf("\n")) + "..."
    } else {
      message
    }
  }

  override def onMessage(event: MessageEvent[PircBotX]): Unit = {
    for (h <- handlers) {
      val m = h.messageMatchRegex.findFirstMatchIn(event.getMessage)
      if (m.isDefined) {
        try {
          h.onMessage(event, m.get)
        } catch {
          case e: ExpectedFailureException => {

          }
          case e: org.jsoup.HttpStatusException => {
            logger.warn("Error executing org.poker.handler", e)
            event.getChannel.send.message(e.getMessage() + s": ${e.getStatusCode}")
          }
          case t: Throwable => {
            logger.warn("Error executing org.poker.handler", t)
            val message = formatExceptionMessage(t.getMessage)
            event.getChannel.send.message(message)
          }
        }
      }
    }
  }
}
