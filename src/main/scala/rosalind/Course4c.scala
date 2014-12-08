//
//Solution to Rosalind challenge "4C: De Bruijn Graph from a String Problem"
//See http://rosalind.info/problems/4c for details.
//
//Given:
//
//    An integer k and a string Text.
//
//    Sample input will look like the following:
//    <pre>
//    4
//    AAGATTCTCTAC
//    </pre>
//
//Return:
//
//    DeBruijn(Text), in the form of an adjacency list.
//
//    Sample output will look like the following:
//    <pre>
//    AAG -> AGA
//    AGA -> GAT
//    ATT -> TTC
//    CTA -> TAC
//    CTC -> TCT
//    GAT -> ATT
//    TCT -> CTA,CTC
//    TTC -> TCT
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

import scala.io.Source
import scala.collection.mutable

object Course4c {


  val FOLDER : String = "c:/downloads/"
  //val FOLDER: String = "C:\\Users\\SCOFIELD\\Documents\\Stash\\Sandbox\\ScalaEx\\IdeaProjects\\Course4c\\data\\"
  val DATA: String = "dataset_199_6.txt"

  /**
   * Read the input.
   * @param path
   * @return Tuple of k and text
   */
  def readinput(path: String): (Int, String) = {
    val lines = Source.fromFile(path).getLines
    val k = lines.next.trim.toInt
    val text = lines.next.trim
    (k, text)
  }

  /**
   * Dump the graph. For debugging.
   * @param graph
   */
  def dumpGraph( graph: mutable.Map[ String, List[String ]] ) : Unit = {
    val iter : Iterator[ (String, List[String]) ] = graph.iterator
    while( iter.hasNext ) {
      val keyValue = iter.next
      //print( keyValue._1 + " => " )
      val iter2 = keyValue._2.iterator
      while( iter2.hasNext ) {
        print( iter2.next + "," )
      }
      println( "" )
    }
  }

  /**
   * Create a DeBruijn graph from DNA string.
   * @param k Size of k-mer
   * @param dna DNA string
   * @return DeBruijn graph
   */
  def makeGraph(k: Int, dna: String): mutable.Map[ String, List[String ]] = {
    var graph = mutable.Map.empty[ String, List[String] ]
    val dnalen = dna.length - ( k - 1 )
    for (i <- 0 until dnalen) {
      val key = dna.substring(i,i+(k-1))
      val value = dna.substring(i+1,i+k)
      //println( "key=" + key + " value=" + value )
      var v = graph.getOrElse( key, Nil )
      if( v.size == 0 ) graph(key) = List(value)
      else {
        v = v :+ value
        graph( key ) = v
      }
    }
    //dumpGraph( graph )
    graph
  }

  /**
   * Print a node of the DeBruijn graph.
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
   * Main execution method. Read the input. Make the graph. Print the results.
   */
  def execute() : Unit = {
    val (k,text) = readinput( FOLDER + DATA )
    //println( "k=" + k + " text=" + text )
    val graph = makeGraph( k, text )
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
