package org.poker.handler

import sx.blah.discord.handle.impl.events.MessageReceivedEvent

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

trait MessageEventHandler {
  val helpMessage: Option[String]
  val messageMatchRegex: Regex

  def onMessage(event: MessageReceivedEvent, firstMatch: Match)
}
