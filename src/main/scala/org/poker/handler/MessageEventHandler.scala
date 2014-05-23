package org.poker.handler

import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.PircBotX

trait MessageEventHandler {
  val helpMessage: Option[String]
  val messageMatchRegex: Regex

  def onMessage(event: MessageEvent[PircBotX], firstMatch: Match)
}
