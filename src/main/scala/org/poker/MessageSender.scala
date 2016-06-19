package org.poker

trait MessageSender {
  def send(message: String): Unit
}
