package org.poker.steam.dota

class MatchDetailsResponse(val result: MatchDetails) {

}

class MatchDetails(val players: List[Player], val radiant_win: Boolean, val duration: Int, val start_time: Int, val match_id: Long,
                    val match_seq_num: Long, val tower_status_radiant: Int, val tower_status_dire: Int, val barracks_status_radiant: Int,
                    val barracks_status_dire: Int, val cluster: Int, val first_blood_time: Int, val lobby_type: Int, val human_players: Int,
                    val leagueid: Int, val positive_votes: Int, val negative_votes: Int, val game_mode: Int) {

}
