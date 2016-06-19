package org.poker.handler

import java.net.URL

import com.stackmob.newman._
import com.stackmob.newman.dsl._
import org.json4s.native.JsonMethods._
import org.poker.ProgramConfiguration
import sx.blah.discord.handle.impl.events.MessageReceivedEvent

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.matching.Regex
import scala.util.matching.Regex.Match

class BitcoinAddressMessageEventHandler(configuration: ProgramConfiguration) extends MessageEventHandler {
  val blockchainUrl = "https://blockchain.info/"

  override val helpMessage: Option[String] = Option("any bitcoin address pasted to the channel will have its transaction information sent to the channel")

  override val messageMatchRegex: Regex = "^[13][a-zA-Z0-9]{26,33}".r

  override def onMessage(event: MessageReceivedEvent, firstMatch: Match): Unit = {
    // TODO: need a custom http client here which doesn't need the right certificate OR need to import the blockchain.info certificate into the trust store
    val message: String = event.getMessage.getContent
    implicit val httpClient = new ApacheHttpClient
    val addressUrl = blockchainUrl + "address/" + message
    val url = new URL(addressUrl + "?format=json")
    val response = Await.result(GET(url).apply, 1.second)
    val singleAddress = parse(response.toJson())
    event.getMessage.getChannel.sendMessage(singleAddress.toString)
  }
}
