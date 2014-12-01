//
//Solution to Rosalind challenge "3D: Greedy Motif Search"
//See http://rosalind.info/problems/3d for details.
//
//Given:
//
//    Integers k and t, followed by a collection of strings Dna.
//
//Return:
//
//    A collection of strings BestMotifs resulting from running
//    GreedyMotifSearch(Dna, k, t). If at any step you find more
//    than one Profile-most probable k-mer in a given string,
//    select the one occurring first in the string.
//
//
//Author: Cary Scofield carys689 <at> gmail <dot> com
//
//Version: 2.11.2
//
//See also: Bioinformatics Algorithms: An Active-Learning Approach by Phillip Compeau & Pavel Pevzner
//
//

package rosalind

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.math._

object Course3d {

  type TwoDArray[A] = Array[Array[A]]

  val USE_PSEUDO_COUNTS : Boolean = false

  val FOLDER : String = ""
  val DATA : String = "rosalind_3d.txt"
  val ACGT_MAP = Map[Char,Int]( 'A' -> 0, 'C'-> 1, 'G' -> 2, 'T' -> 3 )

  def readinput( path : String ) : (Int,Int,List[String]) = {
    val lines = Source.fromFile(path).getLines()
    var kt : Array[Int] = lines.next.split(" ").map(_.toInt)
    var dna = ListBuffer[String]()
    def dnaAppend( dna : ListBuffer[String], line: String ) : Unit = {
      dna += line
    }
    while( lines.hasNext ) {
      dnaAppend( dna, lines.next )
    }
    (kt(0),kt(1),dna.toList)
  }

  def displayInput( data : (Int,Int,List[String] ) ) : Unit = {
    println( "k=" + data._1 + " t=" + data._2 )
    println( "dna=" )
    data._3.foreach( println )
  }

  def dumpMotifs( label : String, motifs : List[String] ) : Unit = {
    val iter : Iterator[String] = motifs.iterator
    while( iter.hasNext ) {
      println( label + iter.next )
    }
  }

  def dump2dArray[A]( label : String, array : TwoDArray[A] ) : Unit = {
    println( label )
    val rowIter = array.iterator
    while( rowIter.hasNext ) {
      val colIter = rowIter.toIterator
      while( colIter.hasNext ) {
        val cols  = colIter.next
        for( i <- 0 until cols.length ) print( cols(i) + " ")
        println("")
      }
    }
  }

  def mostProbableKmer( dna : String, k : Int, profile : TwoDArray[Float] ) : String = {
    //println( "dna=" + dna )
    var bestProbability : Float = -1.0F
    var mostProbable = ""
    var kmer : String = ""
    val dnalen : Int = dna.length - k + 1
    for( i <- 0 until dnalen ) {
      //println( "i=" + i )
      var ki : Int = 0
      var probability : Float = 1.0F;
      kmer = dna.substring( i, i + k )
      //println( "kmer=" + kmer )
      for( j <- 0 until k ) {
        val prob = profile( ACGT_MAP( kmer( j )))(ki)
        probability *= prob
        ki += 1
      }
      if( probability > bestProbability ) {
        bestProbability = probability
        mostProbable = kmer
      }
    }
    //println( "Most probable Kmer=                                " + mostProbable )
    mostProbable
  }

  def score( motifs : List[String] ) : Int = {
    val k : Int = motifs(0).length // assumes all motifs are the same length
    val counts : TwoDArray[Int] = Array.ofDim(4,k)
    val iter : Iterator[String] = motifs.iterator
    while( iter.hasNext ) {
      val motif : String = iter.next
      for( i <- 0 until k ) {
        val nucleotide = motif(i)
        counts(ACGT_MAP( nucleotide))(i) += 1
      }
    }
    //dump2dArray[Int]( "Counts:", counts )
    var sumcnts = List[Int]()
    for( i <- 0 until k ) {
      // find the maximum count in column i
      val maxcnt = max( counts(3)(i), max( counts(2)(i), max( counts(0)(i), counts(1)(i))))
      sumcnts = sumcnts :+ maxcnt
    }
    k * motifs.size - sumcnts.foldLeft(0)(_+_)
  }

  def makeProfile( motifs : List[String] ) : TwoDArray[Float] = {
    var k : Int = motifs(0).length
    var profile : TwoDArray[Float] = Array.ofDim(4,k)
    var counts  : TwoDArray[Int] = Array.ofDim(4,k)

    //dumpMotifs( "motifs:", motifs )
    val iter : Iterator[String] = motifs.iterator
    while( iter.hasNext ) {
      val motif : String = iter.next
      //println( "motif in makeProfile=" + motif )
      for (i <- 0 until k ) {
        //println( "nucleotide=" + motif(i))
        val nucIndex = ACGT_MAP(motif(i))
        //println( "nucIndex=" + nucIndex )
        counts(nucIndex)(i) += 1
      }
    }
    for (i <- 0 until k) {
      for (j <- 0 until 4) {
        if( USE_PSEUDO_COUNTS ) {
          profile(j)(i) = (counts(j)(i) + 1 ) / 4.0F
        }
        else {
          profile(j)(i) = counts(j)(i) / 4.0F
        }
       }
    }
    //dump2dArray[Int]( "Counts:", counts )
    //dump2dArray[Float]( "TwoDFloatArray:", TwoDFloatArray )
    profile
  }

  def greedyMotifSearch( dna: List[String], k : Int, t: Int ) : List[String] = {
    var bestScore : Int = k * t
    var bestMotifs = List[String]()
    var currentProfile : TwoDArray[Float] = Array.ofDim(4,k)
    val dnalen = dna(0).length - k + 1
    for( i <- 0 until dnalen ) {
      var motifs : List[String] = List(dna(0).substring(i,i+k))
      for( j <- 1 until t ) {
        val currentProfile = makeProfile(motifs)
        //dump2dArray[Float]( "currentProfile:", currentProfile )
        motifs = motifs :+ mostProbableKmer( dna(j), k, currentProfile )
      }
      val currentScore = score(motifs)
      //println( "currentScore=" + currentScore )
      if( currentScore < bestScore ) {
        bestScore = currentScore
        bestMotifs = motifs
      }
    }
    bestMotifs
  }

  def execute() : Unit = {
    var (k,t,dna_list) = readinput( FOLDER + DATA )
    displayInput((k,t,dna_list))
    val bestMotifs = greedyMotifSearch( dna_list, k, t )
    println( "Best motifs:" )
    dumpMotifs( "", bestMotifs )
  }

  def  main(args: Array[String]): Unit = {
    execute()
  }
}
