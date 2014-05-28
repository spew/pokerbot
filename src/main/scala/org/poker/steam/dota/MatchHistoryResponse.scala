package org.poker.steam.dota

class MatchHistoryResponse(result: MatchHistoryResponseResult) {
  val matches = result.matches
}

class MatchHistoryResponseResult(val status: Int, num_results: Option[Int], total_results: Option[Int], val matches: List[Match]) {

}

class Match(val match_id: Long, val match_seq_num: Long, val start_time: Long, val lobby_type: Int, val players: List[Player]) {

}
