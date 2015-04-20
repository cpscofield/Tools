package vycegripp

import org.jsoup.Jsoup
import org.jsoup.parser._
import org.jsoup.nodes._
import org.jsoup.select._
import vycegripp.utilities.PageReader
import scala.io.Source
import scala.collection.JavaConversions._
import scala.collection.mutable._

/**
 * This is a simple tool to scrape the page of any stock from the FinViz.com site
 * and produce a list of key-value pairs of the financial data scraped from the page.
 *
 * The program takes a single command line argument which is the ticker symbol for the
 * stock data page wish to scrape, for example: "AAPL", "GOOG", "GE", etc.
 *
 * It uses two external classes:
 * <ul>
 *   <li>The JSoup java Library (from http://jsoup.org)
 *   <li>The PageReader utilities from this Github repository (see src/main/java/vycegripp/utilities/PageReader.java)
 * </ul>
 *
 * author: Cary Scofield (carys689 <at> gmail <dot> com)
 * since: 2.11.2
 */
object FinVizPageScraper {

  /**
   * Return entire page as a single string of HTML code.
   * @param path
   * @return Page as a single string.
   */
  def readinput(path: String): String = {
    val lines = Source.fromFile(path)
    val html = new StringBuilder
    while (lines.hasNext) {
      html.append(lines.next)
    }
    html.toString
  }

  /**
   * Return page from FinVIz site using the URI.
   * @param uri
   * @return Page as single string.
   */
  def readinput(uri: java.net.URI): String = {
    PageReader.readPage(uri).toString()
  }

  val regex = """.*body\=\[(.*)\] offsetx""".r

  def extractDescriptionAndLabel(e: Element): (String, String) = {
    val attr = e.attributes
    val values = regex.findAllIn(attr.get("title"))
    values.hasNext // prevents IllegalStateException; don't know why, but it does.
    val desc = values.group(1).trim
    val label = e.text.trim
    (label, desc)
  }

  /**
   * For each element of row data, produce a tuple containing the name of
   * the data, its value, and a description of the data.
   * @param iter
   * @return
   */
  def extractRowData(iter: Iterator[Element]): (String, String, String) = {
    //var result = ("","","")
    var variable = ""
    var value = ""
    var description = ""
    var elem: Element = null.asInstanceOf[Element]
    elem = iter.next
    if (elem.className.equals("snapshot-td2-cp")) {
      val ld = extractDescriptionAndLabel(elem)
      variable = ld._1
      description = ld._2
    }
    elem = iter.next
    if (elem.className.equals("snapshot-td2")) {
      value = elem.text
    }
    (variable, value, description)
  }

  /**
   * For debugging: Print out table of key-value pairs found in the page.
   * @param table
   */
  def dumpTable(table: scala.collection.mutable.Map[String, String]): Unit = {
    val mapiter = table.iterator
    while (mapiter.hasNext) {
      val (k, v) = mapiter.next
      println(k + "=" + v)
    }
    //println( "# entries=" + table.size )
  }

  def dumpTabLinks(doc: Document): Unit = {
    val p: Elements = doc.getElementsByClass("tab-link")
    val p2 = asScalaBuffer(p)
    val iter = p2.iterator
    while (iter.hasNext) {
      println(iter.next.toString)
    }
  }

  /**
   * Take the JSouped document and produce a list of the key-value pairs of the
   * data found in the document.
   *
   * @param doc
   * @return Map of key-value pairs.
   */
  def parse(doc: Document): scala.collection.mutable.Map[String, String] = {
    var table: scala.collection.mutable.Map[String, String] = new scala.collection.mutable.HashMap[String, String]
    //println( doc )
    //dumpTabLinks( doc )
    val p: Elements = doc.getElementsByClass("table-dark-row")
    val p2 = asScalaBuffer(p)
    val iter = p2.iterator
    while (iter.hasNext) {
      // for each row
      val e = iter.next
      val pc = e.children
      val pc2 = asScalaBuffer(pc)
      val iter2 = pc2.iterator
      while (iter2.hasNext) {
        // for each column
        val data = extractRowData(iter2) // variable name, value, and description returned
        table = table.+((data._1, data._2)) // add variable name and value to table
      }
    }
    table
  }

  /**
   * Using JSoup, process the page and produce a linearized version of the HTML (every element tag
   * on a separate line) but still as a legitimate Document.
   * @param html
   * @return The document.
   */
  def soupIt(html: String): Document = {
    Jsoup.parse(html)
  }

  def main(args: Array[String]): Unit = {
    //val html = readinput("C:\\Users\\SCOFIELD\\Documents\\temp\\AAPL.html")
    val html = readinput(new java.net.URI("http://finviz.com/quote.ashx?t=" + args(0)));
    val table = parse(soupIt(html)) // produces a map of key-value pairs of the financial data found in the page
    dumpTable(table)
  }
}
