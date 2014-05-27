package org.poker.scc

import org.poker.ProgramConfiguration
import org.jsoup.{Connection, Jsoup}
import scala.collection.JavaConversions.mapAsScalaMap
import scala.collection.JavaConversions.mapAsJavaMap
import scala.collection.mutable
import org.jsoup.nodes.{Element, Document}
import org.jsoup.Connection.Method
import org.jsoup.select.Elements
import com.google.api.client.util.Lists
import com.google.api.client.repackaged.com.google.common.base.Strings
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

class SceneAccessClient(configuration: ProgramConfiguration) {
  val url = "https://sceneaccess.eu"
  private var cookies = mutable.Map[String, String]()

  private def login(response: Connection.Response): Unit = {
    val loginResponse = Jsoup.connect(response.url.toString)
      .data("username", configuration.sceneAccessUserName.get)
      .data("password", configuration.sceneAccessPassword.get)
      .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
      .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.45 Safari/537.36")
      .followRedirects(true)
      .cookies(response.cookies)
      .method(Connection.Method.POST)
      .execute
    cookies = loginResponse.cookies
  }

  def findShow(showName: String): List[SceneTorrent] = {
    val searchResponse: Connection.Response = Jsoup.connect(url.toString + "/browse?search=" + showName + "&method=2&c27=27")
      .cookies(cookies)
      .method(Method.GET)
      .timeout(5000)
      .execute
    if (!searchResponse.url.toString.toLowerCase.contains("browse?search")) {
      this.login(searchResponse)
      return this.findShow(showName)
    }
    val document = searchResponse.parse
    val rowElements: Elements = document.select("tr.tt_row")

    val torrents = mutable.MutableList[SceneTorrent]()
    import scala.collection.JavaConversions._
    for (elem <- rowElements) {
      val aDetails: Element = elem.select("td.ttr_name").first.select("a").first
      val detailsUrl: String = url + "/" + aDetails.attr("href")
      val detailsDocument = Jsoup.connect(detailsUrl).cookies(cookies).get
      val titleElement: Element = detailsDocument.select("span.fls").first
      if (!(titleElement == null || Strings.isNullOrEmpty(titleElement.text) || Strings.isNullOrEmpty(aDetails.attr("href")))) {
        val detailsTable: Element = detailsDocument.select("table#details_table").first
        var dateAdded: DateTime = null
        import scala.collection.JavaConversions._
        for (tableRowElement <- detailsTable.select("tr")) {
          val tdElement = Option(tableRowElement.select("td.td_head").first)
          if (tdElement.isDefined) {
            if (tdElement.get.text.trim.toLowerCase() == "added") {
              val tdColumn: Element = tableRowElement.select("td.td_col").first
              val formatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC()
              dateAdded = formatter.parseDateTime(tdColumn.text)
            }

          }
        }
        val t = new SceneTorrent(titleElement.text, aDetails.attr("href"), dateAdded)
        torrents += t
      }
    }
    return torrents.toList
  }
}
