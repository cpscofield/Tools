//
// From Chapter 33 "Programming in Scala, 2nd Edition" by Odersky, Spoon, and Venners.
//

package vycegripp

import scala.util.parsing.combinator._
import scala.util.{Try, Success, Failure}

object JsonParser {

   class JSON1 extends JavaTokenParsers {
    def obj: Parser[Map[String, Any]] =
      "{" ~> repsep(member, ",") <~ "}" ^^ (Map() ++ _)

    def arr: Parser[List[Any]] =
      "[" ~> repsep(value, ",") <~ "]"

    def member: Parser[(String, Any)] =
      stringLiteral ~ ":" ~ value ^^ { case name ~ ":" ~ value => (name, value) }

    def value: Parser[Any] = (
      obj
        | arr
        | stringLiteral
        | floatingPointNumber ^^ (_.toDouble)
        | "null"  ^^ (x => null)
        | "true"  ^^ (x => true)
        | "false" ^^ (x => false)
      )
  }

  case class Notification(json:String) {
    private val parser : JSON1 = new JSON1
    private var rcpts : List[Map[String,Any]] = Nil
    private var msg : Map[String,Any] = Map()
    private var transId : Option[String] = None
    private var serviceName : Option[String] = None
    //def parse(json:String) : Try[String] = Failure(new Exception()) // for now...
    def parse(json:String) : Try[String] = {
      val result : parser.ParseResult[Map[String,Any]] = parser.parseAll(parser.obj,json)

    }
    def getRecipients():List[Map[String,Any]] = {rcpts} // for now...
    def getMessage():String = {msg.getOrElse(""""message"""","")} // for now...
    def getVersion():Double = {msg.getOrElse(""""version"""","")} // for now...
    def getTransactionId():Option[String] = None // for now...
    def getServiceName():Option[String] = None // for now...
  }

  def main(args:Array[String]): Unit = {
    var i=0
    val parser : JSON1 = new JSON1
    val startTime = System.currentTimeMillis()
    val result : parser.ParseResult[Map[String,Any]] = parser.parseAll(parser.obj,msg)
    println( System.currentTimeMillis() - startTime + " millisecs to parse" )
    println((i=i+1) + "=" + result.getOrElse("",println("parser error")))
    val somethingElse = result.get
    println((i=i+1) + "=" + somethingElse.getOrElse(""""service_name"""","no service name"))
    somethingElse.foreach(println)
    val keysIter = somethingElse.keysIterator
    while(keysIter.hasNext) {
      val key = keysIter.next
      val value = somethingElse.get(key)
      println((i=i+1) + "=" + "key="+key+" value="+value)
    }
//    println((i=i+1) + "=" + somethingElse.keys)
//    println((i=i+1) + "=" + somethingElse.size)
//    println((i=i+1) + "=" + somethingElse.toMap)
//    println((i=i+1) + "=" + somethingElse.getOrElse("does not exist","does not exist"))
//    println((i=i+1) + "=" + somethingElse.get("does not exist"))
//    println((i=i+1) + "=" + somethingElse.toMap.getOrElse(""""transactionId"""","no trans id"))
//    println((i=i+1) + "=" + somethingElse.toMap.get(""""transactionId""""))

    // This is the one we want
    println("this one=" + somethingElse.getOrElse(""""transactionId"""",None))
    //

    val rcpts = somethingElse.getOrElse(""""rcpts"""", None)
    val riter = rcpts.asInstanceOf[List[Map[String,Any]]].iterator
    while(riter.hasNext){
      val rmap = riter.next
      val device = rmap.getOrElse(""""device"""", "no device")
      val deviceId = rmap.getOrElse(""""deviceId"""","no device id")
      println(s"device=$device deviceId=$deviceId")
    }

    val serviceName = somethingElse.getOrElse(""""service_name""",None)
    println(s"serviceNam=$serviceName")
    val transId = somethingElse.getOrElse(""""transactionId"""",None)
    println(s"transid=$transId")

    val message = somethingElse.getOrElse(""""msg"""",None)
    val msgMap = message.asInstanceOf[Map[String,Any]]
    val theMessage = msgMap.getOrElse(""""message"""",None)
    val theVersion =  msgMap.getOrElse(""""version"""",None)
    println(s"message=$theMessage version=$theVersion")

//    println((i=i+1) + "=" + somethingElse.get(""""transactionId""""))
    println((i=i+1) + "=" + somethingElse.get(""""rcpts""""))
//    println((i=i+1) + "=" + somethingElse.get(""""msg""""))
//    println((i=i+1) + "=" + result)
  }

  val msg =
    """
      | {
      |   "rcpts":
      |   [
      |     {
      |     "device":"DVR",
      |     "deviceId":"device1234"
      |     },
      |     {
      |     "device":"DVR",
      |     "deviceId":"device5678"
      |     }
      |   ],
      |   "service_name":"DVR reboot",
      |   "transactionId":"0123456789",
      |   "msg":
      |   {
      |     "version":1,
      |     "message":"This is the message"
      |   }
      |
    """.stripMargin

}
