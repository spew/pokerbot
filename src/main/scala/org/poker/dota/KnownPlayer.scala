package org.poker.dota

package object KnownPlayers {
  val steven = new KnownPlayer(28326143L, List("bunk", "steven"), true)

  val all = new KnownPlayer(38926297L, List("whitey", "pete"), false)::
    new KnownPlayer(80342375L, List("bertkc", "brett", "bank", "gorby"), true)::
    new KnownPlayer(28308237L, List("mike"), true)::
    new KnownPlayer(10648475L, List("fud", "spew", "deathdealer69"), true)::
    steven::
    new KnownPlayer(125412282L, List("mark", "clock", "cl0ck"), true)::
    new KnownPlayer(81397072L, List("clock2", "cl0ck2"), true)::
    new KnownPlayer(78932949L, List("muiy", "dank"), true)::
    new KnownPlayer(34117856L, List("viju", "vijal"), true)::
    new KnownPlayer(29508928L, List("sysm"), true)::
    new KnownPlayer(32387791L, List("ctide", "chris", "tide"), true)::
    new KnownPlayer(49941053L, List("abduhl", "jake"), true)::
    new KnownPlayer(32385879L, List("tbs", "tom"), true)::
    new KnownPlayer(40737752L, List("fourk"), true)::
    new KnownPlayer(12855832L, List("hed", "handsomehed", "xhedx"), true)::
    new KnownPlayer(2462417L, List("shaftian"), true)::
    Nil
}

class KnownPlayer(val id: Long, val aliases: List[String], val enabledForPing: Boolean = true) {

}
