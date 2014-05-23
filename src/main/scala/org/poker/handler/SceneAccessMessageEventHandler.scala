package org.poker.handler

import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.PircBotX
import org.poker.scc.SceneAccessClient
import org.poker.ProgramConfiguration

class SceneAccessMessageEventHandler(configuration: ProgramConfiguration) extends MessageEventHandler {
  val sceneAccessClient = new SceneAccessClient(configuration)

  override val helpMessage: Option[String] = Option("!scene <title>: send the latest uploaded to sceneaccess for <title>")

  override val messageMatchRegex: Regex = "[!.](?i)((scc)|(scene)) ?(?<title>.*)".r

  override def onMessage(event: MessageEvent[PircBotX], firstMatch: Match): Unit = {
    val title = firstMatch.group(4)
    if (configuration.sceneAccessPassword.isDefined && configuration.sceneAccessUserName.isDefined) {
      sendSearch(event, title)
    } else {
      event.getChannel.send.message("unable to query sceneaccess: invalid configuration")
    }
  }

  private def sendSearch(event: MessageEvent[PircBotX], title: String): Unit = {
    if (title.isEmpty) {
      event.getChannel.send().message("usage: !scene <title>")
    } else {
      val torrents = sceneAccessClient.findShow(title)
      if (torrents.isEmpty) {
        event.getChannel.send.message(s"unable to find anything on the scene for '${title}'")
      } else {
        val torrent = torrents.head
        val url = sceneAccessClient.url + "/" + torrent.url
        val formattedDate = torrent.dateAdded.toString("yyyy-MM-dd")
        event.getChannel.send.message(s"${torrent.title} | ${formattedDate} | 720p | ${url}")
      }
    }
  }
}
