//============================================================================
//
//Solution to Rosalind challenge "FIB: Rabbits and Recurrence Relations"
//See http://rosalind.info/problems/fib for details.
//
//Given:
//
//    Positive integers n≤40 and k≤5.
//
//Return:
//
//    The total number of rabbit pairs that will be present after
//    n months if we begin with 1 pair and in each generation,
//    every pair of reproduction-age rabbits produces a litter of
//    k rabbit pairs (instead of only 1 pair).
//
//Author:
//
//    Cary Scofield
//    carys689 <at> gmail <dot> com
//
//============================================================================

package rosalind

import scala.io.Source
import java.lang.System

object Fib {

  def readinput( path : String ) : (Int,Int) = {
    val f = Source.fromFile( path )
    val t = f.bufferedReader().readLine().split( " ").map( _.toInt )
    ( t(0), t(1) )
  }

  def fib( n : Long, k : Long ) : Long = {
    //println( "n=" + n + " k=" + k )
    if( n < 3 ) 1
    else fib( n - 1, k ) + k * fib( n - 2, k )
  }

  def execute() {
    val (n : Int, k : Int ) = readinput( "c:/downloads/rosalind_fib.txt" )
    println( fib( n, k ) )
  }

  def main( args : Array[String] ) {
    val start = System.currentTimeMillis()
    execute()
    val end = (System.currentTimeMillis() - start)/1000.0D
    println( f"Total time=$end%.3f second(s)" )
  }

}
