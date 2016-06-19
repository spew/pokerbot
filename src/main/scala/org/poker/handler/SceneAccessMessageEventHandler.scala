package org.poker.handler

import org.poker.ProgramConfiguration
import org.poker.scc.{SceneAccessClient, TorrentFormatter}
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

import scala.util.matching.Regex
import scala.util.matching.Regex.Match

class SceneAccessMessageEventHandler(configuration: ProgramConfiguration) extends MessageEventHandler {
  val sceneAccessClient = new SceneAccessClient(configuration)

  override val helpMessage: Option[String] = Option("!scene <title>: send the latest uploaded to sceneaccess for <title>")

  override val messageMatchRegex: Regex = "^[!.](?i)((scc)|(scene)) ?(?<title>.*)".r

  override def onMessage(event: MessageReceivedEvent, firstMatch: Match): Unit = {
    val title = firstMatch.group(4)
    if (configuration.sceneAccessPassword.isDefined && configuration.sceneAccessUserName.isDefined) {
      sendSearch(event, title)
    } else {
      event.getMessage.getChannel.sendMessage("unable to query sceneaccess: invalid configuration")
    }
  }

  private def sendSearch(event: MessageReceivedEvent, title: String): Unit = {
    if (title.isEmpty) {
      event.getMessage.getChannel.sendMessage("usage: !scene <title>")
    } else {
      val torrents = sceneAccessClient.findShow(title)
      if (torrents.isEmpty) {
        event.getMessage.getChannel.sendMessage(s"unable to find anything on the scene for '${title}'")
      } else {
        val torrent = torrents.head
        val message = new TorrentFormatter().format(torrent, sceneAccessClient)
        event.getMessage.getChannel.sendMessage(message)
      }
    }
  }
}
