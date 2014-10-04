package org.poker.dota

import org.poker.steam.SteamClient

class LatestMatchFinder(val steamClient: SteamClient) {
  def findLatestMatch() = {
    val channelPlayers = KnownPlayers.all
    val result = channelPlayers.filter(p => p.enabledForPing).map(p => steamClient.getLatestDotaMatches(p.id, 1)).flatten
    val sorted = result.sortBy(m => m.start_time).reverse
    val latestDetails = sorted.take(Math.min(3, sorted.size)).map(m => steamClient.getDotaMatchDetails(m.match_id))
    latestDetails.sortBy(m => m.start_time + m.duration).last
  }
}
