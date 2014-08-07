package org.poker.util

import java.util.concurrent.ThreadFactory

import scala.collection.mutable

class DaemonThreadFactory extends ThreadFactory {
  val threads = mutable.MutableList[Thread]()
  // TODO: add way to set thread name prefix
  override def newThread(r: Runnable): Thread = {
    val t = new Thread(r)
    t.setDaemon(true)
    threads += (t)
    t
  }
}
