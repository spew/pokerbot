package org.poker.untapped

class SearchResponse(val message: String, val brewery_id: Boolean, val type_id: Int, val search_version: Int, val found: Int,
                      val offset: Int, val limit: Int, val term: String, val parsed_term: String, val beers: SearchResultList) {

}
