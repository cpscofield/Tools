//
//Solution to Rosalind challenge "4B: Overlap Graph Problem"
//See http://rosalind.info/problems/4b for details.
//
//Given:
//
//    A collection Patterns of k-mers.
//
//Return:
//
//    The overlap graph Overlap(Patterns), in the form of an adjacency list
//    similar to something like:
//    <pre>
//    AGGCA -> GGCAT
//    CATGC -> ATGCG
//    GCATG -> CATGC
//    GGCAT -> GCATG
//    </pre>
//
//
//Author: Cary Scofield carys689 <at> gmail <dot> com
//
//Scala Version: 2.11.2
//
//See also: Bioinformatics Algorithms: An Active-Learning Approach
//    by Phillip Compeau & Pavel Pevzner
//
//
package rosalind

import scala.annotation.tailrec
import scala.io.Source
import java.io._
import scala.collection.mutable.ListBuffer
import scala.math.Ordering
import scala.math._

object SubStringOrdering extends Ordering[String] {
 def compare( a: String, b: String ): Int = {
   a.substring(1,a.length) compare b.substring(1,b.length)
 }
}

object OrderingByTuple1stParam extends Ordering[(String,String)] {
  def compare( pair1: (String,String), pair2: (String,String)  ) : Int = {
    pair1._1 compare pair2._1
  }
}

object Course4b {

  val FOLDER : String = "c:/downloads/"
  val DATA   : String = "dataset_198_9 (3).txt"

  /**
   * Read the input
   * @param path
   * @return List of k-mer strings.
   */
  def readinput( path : String ) : List[String] = {
    val lines = Source.fromFile( path ).getLines
    var kmers = ListBuffer[String]()
    while( lines.hasNext ) {
      kmers += lines.next.trim
    }
    kmers.toList
  }

  /**
   * Calculate overlap graph. Recursive version.
   * @param kmers1 K-mer string, sorted by suffix.
   * @param kmers2 K-mer string sorted by prevfix.
   * @return Adjacency list of k-mer parirs.
   */
  def calcOverlapGraph( kmers1 : List[String], kmers2 : List[String] ) : List[(String,String)] = {
    var adjacencyList = List[(String,String)]()
    def checkAdjacency( kmers1 :List[String], kmers2 : List[String]) : Unit = {
      (kmers1,kmers2) match {
        case( Nil, Nil ) => return
        case (Nil, _   ) => return
        case( _,   Nil ) => return
        case( _,   _   ) => {
          val k1 = kmers1.head
          val k2 = kmers2.head
          //println( "Comparing " + k1 + " with " + k2 )
          if( !k1.equals( k2 ) ) {
            if( k1.substring( 1, k1.length).equals( k2.substring( 0, k2.length-1) ) ) {
              //println( k1 + " -> " + k2 )
              adjacencyList = adjacencyList :+ ( k1, k2 )
              checkAdjacency( kmers1.tail, kmers2.tail )
            }
          }
          checkAdjacency( kmers1, kmers2.tail )
        }
      }
    }
    checkAdjacency( kmers1, kmers2 )
    adjacencyList.sorted(OrderingByTuple1stParam)
  }

  /**
   * Compare two strings.
   * @param a
   * @param b
   * @return -1 if a precedes b, +1 if b precedes a, or 0 if a is equal to b
   */
  def compareKmers( a : String, b : String ) : Int = {
    val minlen : Int = if( a.length < b.length ) a.length else b.length
    for( i <- 0 until minlen ) {
      if( a(i) < b(i) ) return -1
      else if( a(i) > b(i) ) return +1
    }
    if( a.length < b.length ) return -1
    else if( a.length > b.length ) return +1
    else return 0
  }


  /**
   * Calculate overlap graph. Iterative version.
   * @param kmers1 K-mer string, sorted by suffix.
   * @param kmers2 K-mer string sorted by prefix.
   * @return Adjacency list of k-mer parirs.
   */
  def calcOverlapGraph2( kmers1 : List[String], kmers2 : List[String] ) : List[(String,String)] = {
    var adjacencyList = List[(String, String)]()
    var _kmers1 = kmers1
    var _kmers2 = kmers2
    while (_kmers1 != Nil) {
      while (_kmers2 != Nil) {
        val k1 = _kmers1.head
        val k2 = _kmers2.head
        //println( "Comparing " + k1 + " with " + k2 )
        if( !k1.equals( k2 ) ) {
          val result = compareKmers(k1.substring(1, k1.length), k2.substring(0, k2.length - 1))
          result match {
            case -1 => { // k1 < k2
              _kmers1 = _kmers1.tail
            }
            case 1 => { // k1 > k2
              _kmers2 = _kmers2.tail
            }
            case 0 => { // k1 == k2
              //println( k1 + " -> " + k2 )
              adjacencyList = adjacencyList :+ (k1, k2)
              _kmers1 = _kmers1.tail
              _kmers2 = _kmers2.tail
            }
          }
        }
      }
    }
    adjacencyList
  }

  /**
   * Main execution method. Read in k-mer list.
   * Sort by 2nd through nth characters.
   * Duplicate k-mer list and sort by 1st through n-1th characters.
   * Calculate and then print out adjacency list of k-mer pairs.
   * @return
   */
  def execute() : Unit = {
    val kmers = readinput( FOLDER + DATA ).sorted( SubStringOrdering )
    val kmersDuped = kmers.sorted
    //println( kmers.length + " k-mers in input" )
    val adjacencyList = calcOverlapGraph2( kmers, kmersDuped )
    for( t <- adjacencyList ) {
      println( t._1 + " -> " + t._2 )
    }
  }

  def main( args : Array[String]) : Unit = {
    val startTime = System.currentTimeMillis()
    execute()
    val endTime = System.currentTimeMillis();
    println( "\nTotal time: " + ((endTime-startTime)/1000.0F) + " second(s)" )
  }
}
