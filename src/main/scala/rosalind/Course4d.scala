//
//Solution to Rosalind challenge "4D: De Bruijn Graph from k-mers Problem"
//See http://rosalind.info/problems/4d for details.
//
//Given:
//
//    A collection of k-mers Patterns.
//
//    Sample input will look like the following:
//    <pre>
//    GAGG
//    CAGG
//    GGGG
//    GGGA
//    CAGG
//    AGGG
//    GGAG
//    </pre>
//
//Return:
//
//    The de Bruijn graph DeBruijn(Patterns), in the form of an adjacency list.
//
//    Sample output will look like the following:
//    <pre>
//    AGG -> GGG
//    CAG -> AGG,AGG
//    GAG -> AGG
//    GGA -> GAG
//    GGG -> GGA,GGG
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

import scala.collection.mutable
import scala.io.Source
import scala.collection.mutable.ListBuffer

object Course4d {

  val FOLDER : String = "c:/downloads/"
  //val FOLDER: String = "C:\\Users\\SCOFIELD\\Documents\\Stash\\Sandbox\\ScalaEx\\IdeaProjects\\Course4d\\data\\"
  val DATA: String = "dataset_200_7.txt"

  /**
   * Read input date.
   * @param path
   * @return List of k-mers
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
   * Dump DeBruijn graph. For debugging.
   * @param graph
   */
  def dumpGraph( graph: mutable.Map[ String, List[String ]] ) : Unit = {
    val iter : Iterator[ (String, List[String]) ] = graph.iterator
    while( iter.hasNext ) {
      val keyValue = iter.next
      print( keyValue._1 + " => " )
      val iter2 = keyValue._2.iterator
      while( iter2.hasNext ) {
        print( iter2.next + "," )
      }
      println( "" )
    }
  }

  /**
   * Create a DeBruijn graph from list of k-mers
   * @param kmers
   * @return DeBruijn graph
   */
  def makeGraph( kmers : List[ String ] ) : mutable.Map[String, List[String] ] = {
    var graph = mutable.Map.empty[String, List[String]]
    val klen: Int = kmers(0).length
    val iter = kmers.iterator
    while( iter.hasNext ) {
      val kmer = iter.next
      val prefix = kmer.substring( 0, klen - 1 )
      val suffix = kmer.substring( 1, klen )
      var pvalue = graph.getOrElse( prefix, Nil )
      if( pvalue.size == 0 ) {
        graph( prefix ) = List(suffix)
      }
      else {
        pvalue = pvalue :+ suffix
        graph( prefix ) = pvalue
      }
    }
    //dumpGraph( graph )
    graph
  }

  /**
   * Print DeBruijn node
   * @param node
   */
  def printNode( node : (String,List[String]) ) : Unit = {
    print( node._1 + " -> " )
    for( i <- 0 until node._2.size ) {
      if( i > 0 ) print( "," )
      print( node._2(i))
    }
    println( "" )
  }

  /**
   * Main execute method. Read data. Create graph. Output results.
   */
  def execute() : Unit = {
    val kmers = readinput( FOLDER + DATA )
    val graph = makeGraph( kmers )
    for( g <- graph ) {
      printNode( g )
    }

  }


  def main( args : Array[String]) : Unit = {
    val startTime = System.currentTimeMillis()
    execute()
    val endTime = System.currentTimeMillis();
    println( "\nTotal time: " + ((endTime-startTime)/1000.0F) + " second(s)" )
  }


}
