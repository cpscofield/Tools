package vycegripp

/**
 * <p>
 * This program checks the http links found in a web page. Program takes
 * one argument which is the URI of the web page to be checked.
 *
 * @since Scala 2.11; Akka 2.3.4
 * @author Cary Scofield (carys689 <at> gmail <dot> com)
 * @date June 2015;
 */

import akka.actor._
import akka.routing._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger
import scala.util.control.Breaks._

object LinkCheckerMT {

  type LinksType = ConcurrentLinkedQueue[String]

  val NUM_WORKERS = 8

  sealed trait Message
  case class CheckLinks() extends Message
  case class Work(link:String) extends Message
  case class BadLink(link:String) extends Message
  case class GoodLink() extends Message
  case class Shutdown(numBadLinks:Int,millisSeconds:Long) extends Message

  def main(args: Array[String]): Unit = {
    import vycegripp.utilities.PageReader
    try {
      val pageToTest = args(0)
      println("Testing links on page: " + pageToTest)
      val page: java.lang.StringBuilder = PageReader.readPage(new java.net.URI(pageToTest))
      val links = getLinksFromPage(page.toString)
      if(links.size==0) {
        println("No anchor or option http links found on page")
        System.exit(0)
      }
      val system = ActorSystem("LinkChecker")
      val listener = system.actorOf(Props[Listener], name = "listener")
      val master: ActorRef = system.actorOf(Props(new Master(NUM_WORKERS, links, listener)), name = "master")
      println("Sending CheckLinks message to Master")
      master ! CheckLinks()
      //      try { Thread.sleep(15000)} catch{ case _: Throwable => }
      //      system.shutdown()
    }
    catch {
      case e: Exception => e.printStackTrace
        System.exit(1)
    }
  }

  class Master(numWorkers: Int, links: LinksType, listener:ActorRef) extends Actor with ActorLogging {
    val numLinks = links.size
    log.info("Master actor started up; #links=" + numLinks)
    val linksTested : AtomicInteger  = new AtomicInteger(0);
    val badLinks: LinksType = new LinksType
    val workerRouter = context.actorOf(
      Props[Worker].withRouter(RoundRobinPool(numWorkers)), name = "workerRouter")
    val startTime : Long = System.currentTimeMillis()

    def receive = {
      case CheckLinks() =>
        //
        // Send all page links to the workers via the router
        //
        log.info("Master: CheckLinks message received")
        for (i <- 0 until numLinks) {
          try {
            val link: String = links.poll
            //
            // Send link to be tested to a worker
            //
            workerRouter ! Work(link)

         }
          catch {
            //
            // This should never happen
            //
            case e: NoSuchElementException => println(e.toString)
          }
        }

      case BadLink(link: String) =>
        //
        // Receive notification of a bad link from a work
        //
        log.info("Bad link found: " + link)
        badLinks.add(link)
        linksTested.getAndIncrement
        log.info("Tested " + linksTested.get() + " links; " + ( numLinks - linksTested.get()  + " to go."))
        if(linksTested.get() == numLinks) {
          listener ! Shutdown(badLinks.size,System.currentTimeMillis-startTime)
        }

      case GoodLink() =>
        linksTested.getAndIncrement
        log.info("Tested " + linksTested.get() + " links; " + ( numLinks - linksTested.get()  + " to go."))
        if(linksTested.get() == numLinks) {
          listener ! Shutdown(badLinks.size,System.currentTimeMillis-startTime)
        }

      case _ => log.info("Master actor got unexpected message")

    }

  }

  class Listener extends Actor with ActorLogging {
    def receive = {
      case Shutdown(numBadLinks:Int, milliSecs:Long) =>
        log.info( "LinkChecker is complete and shutting down" )
        log.info( s"$numBadLinks bad links found.")
        log.info( "Total execution time: " + (milliSecs/1000.0D) + " seconds.")
        context.system.shutdown()
    }
  }

  class Worker extends Actor with ActorLogging {
    log.info("Worker actor started up")
    var okay: Boolean = false

    def receive = {
      case Work(link: String) =>
        //
        // Check connectivity to a link
        //
//        log.info("Worker: Work message received")
        try {
          okay = checkLink(new java.net.URI(link))
        }
        catch {
          case e: Exception => println(e.toString)
        }
        if (!okay) {
//          log.info(s"Bad link: $link")
          //
          // Notify sender of unaccessible link
          //
          sender ! BadLink(link)
        }
        else {
          sender ! GoodLink()
        }

      case _ => log.info("Worker actor got unexpected message")

    }

    //
    // Check the link. Return <tt>true</tt> if valid; otherwise return <tt>false</tt>.
    //
    def checkLink(uri: java.net.URI): Boolean = {
      val CONNECTION_TIMEOUT: Int = 15000
      var valid: Boolean = false
      var is: java.io.InputStream = null
      try {
        val url: java.net.URL = uri.toURL
        val connection: java.net.URLConnection = url.openConnection()
        connection.setConnectTimeout(CONNECTION_TIMEOUT)
        val PAGE_SIZE = 4096
        val NUM_PAGES = 1
        is = url.openStream()
        val bis = new java.io.BufferedInputStream(is, PAGE_SIZE * NUM_PAGES)
        if (is != null) {
          valid = true
          is.close
        }
      }
      catch {
        case e: Exception =>
          if (uri.toString.indexOf("finance.yahoo.com") != -1) e.printStackTrace
          try {
            if (is != null) is.close
          } catch {
            case _: Throwable => /* ignore */
          }
      }
      valid
    }
  }

  def getLinksFromPage(page: String) : LinksType = {
    val links : LinksType = new LinksType
    val document: Document = Jsoup.parse(page)
    extractLinksFromTags(document, "A", "href", links)
    extractLinksFromTags(document, "OPTION", "value", links)
    links
  }

  def extractLinksFromTags(document: Document, tag: String, attributeName: String, linksToTest: LinksType): Unit = {
    import org.jsoup.nodes.{Element, Attributes, Attribute}
    import org.jsoup.select.Elements
    val elements: Elements = document.getElementsByTag(tag)
    val eIter = elements.iterator()
    while (eIter.hasNext) {
      val element: Element = eIter.next
      val attributes: Attributes = element.attributes
      val aIter = attributes.iterator()
      breakable {
        while (aIter.hasNext) {
          val attribute: Attribute = aIter.next
          val key: String = attribute.getKey
          if (key.toLowerCase.equals(attributeName)) {
            val value: String = attribute.getValue.toLowerCase
            if (value.trim.length > 0 && value.startsWith("http")) {
              linksToTest.add(stripQueryString(value))
              break
            }
          }
        }
      }
    }
  }

  def stripQueryString(link: String): String = {
    val beginQuery = link.indexOf("?")
    val queryPresent = beginQuery != -1
    if (queryPresent) {
      link.substring(0, beginQuery)
    }
    else {
      link
    }
  }

}
