package org.poker.handler

import java.lang.management.ManagementFactory

import sx.blah.discord.handle.impl.events.MessageReceivedEvent

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

class InfoMessageEventHandler extends MessageEventHandler {
  override val helpMessage: Option[String] = Option("!info: send information about the bot to channel")

  override val messageMatchRegex: Regex = "^[.!](?i)info".r

  override def onMessage(event: MessageReceivedEvent, firstMatch: Match): Unit = {
    val runtime = Runtime.getRuntime
    val megabyte = (1024 * 1024).toDouble
    val usedMemory = "%1.2f".format((runtime.totalMemory() - runtime.freeMemory()) / megabyte)
    val totalMemory = "%1.2f".format(runtime.totalMemory() / megabyte)
    var message = s"os: ${util.Properties.osName} | java: ${util.Properties.javaVendor} ${util.Properties.javaVersion} | scala: ${util.Properties.versionNumberString}"
    message += s" | memory: ${usedMemory}/${totalMemory} MB"
    val threadCount = ManagementFactory.getThreadMXBean.getThreadCount
    message += s" | threads: ${threadCount}"
    event.getMessage.getChannel.sendMessage(message)
  }
}
