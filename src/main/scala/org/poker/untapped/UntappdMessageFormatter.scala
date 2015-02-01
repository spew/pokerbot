package org.poker.untapped

import java.text.SimpleDateFormat

import com.github.nscala_time.time.Imports._
import org.poker.util.RelativeTimeFormatter

object UntappdMessageFormatter {
  private val dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z")
  def formatCheckinMessage(checkin: Checkin, beer: Beer) = {
    val rating = formatRating(beer.rating_score)
    val url = s"http://untappd.com/user/${checkin.user.user_name}/checkin/${checkin.checkin_id}"
    val venueMessage = if (checkin.venue.isDefined) s"at '${checkin.venue.get.venue_name}' " else ""
    val checkinTime = new DateTime(dateFormatter.parse(checkin.created_at))
    val relativeTime = RelativeTimeFormatter.relativeToNow(checkinTime)
    val checkinMessage = if (checkin.checkin_comment == "") "" else s"said '${checkin.checkin_comment}' and "
    if (checkin.rating_score > 0) {
      s"${checkin.user.user_name} ${checkinMessage}rated '${checkin.beer.beer_name} (${beer.brewery.brewery_name})' ${checkin.rating_score}/5.0 (avg ${rating}) ${venueMessage}${relativeTime}: ${url}"
    } else {
      s"${checkin.user.user_name} ${checkinMessage}declined to rate '${checkin.beer.beer_name} (${beer.brewery.brewery_name})' (avg ${rating}) ${venueMessage}${relativeTime}: ${url}"
    }
  }

  private def formatRating(rating: Double) = {
    f"${rating}%1.1f"
  }
}
