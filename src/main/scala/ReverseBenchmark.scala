import scala.util.Random

/**
 * This program is a benchmarking of various list reversal algorithms written by
 * Phil Gold as presented in <a href="http://aperiodic.net/phil/scala/s-99">S-99 Ninety-Nine Scala Problem</a>
 * Specifically, the reverse algorithms are verbatim from Problem P05.
 * <p>
 * Running the program on a dual-core, 2.7 GHz, Windows 7 system can produce
 * benchmark results similar to the following:
 * <pre>
 *   Benchmarking the list reversal of 1000 items
 *   ======================================================
 *   Built-in reverse: 1 milliseconds
 *   Recursive reverse: 39 milliseconds
 *   Tail recursive reverse: 1 milliseconds
 *   Functional reverse: 3 milliseconds
 * </pre>
 *
 * @author Cary Scofield (carys689 <at> gmail <dot> com)
 * @version 2.11.2
 *
 * <p>
 * There are no copyright claims on this code. Use at your own risk.
 */

object ReverseBenchmark {

  // Simple recursive.  O(n^2)
  def reverseRecursive[A](ls: List[A]): List[A] = ls match {
    case Nil       => Nil
    case h :: tail => reverseRecursive(tail) ::: List(h)
  }

  // Tail recursive.
  def reverseTailRecursive[A](ls: List[A]): List[A] = {
    def reverseR(result: List[A], curList: List[A]): List[A] = curList match {
      case Nil       => result
      case h :: tail => reverseR(h :: result, tail)
    }
    reverseR(Nil, ls)
  }

  // Pure functional
  def reverseFunctional[A](ls: List[A]): List[A] =
    ls.foldLeft(List[A]()) { (r, h) => h :: r }

  def benchmark[A]( title : String, f: List[A] => List[A], list:List[A]  ) : Unit = {
    val startTime = System.currentTimeMillis()
    f(list)
    println ( title + ": " + ( System.currentTimeMillis() - startTime ) + " milliseconds" )
  }

  def main(args: Array[String]): Unit = {
    val numIntegers: Int = 1000
    var startTime : Long = 0
    var endTime : Long = 0
    val numbers : List[Int] = List.fill(numIntegers)(Random.nextInt).sorted

    numbers.reverse // do this only to 'prime' the cache before the actual benchmarking

    println( "Benchmarking the list reversal of " + numIntegers + " items" )
    println( "======================================================" )

    startTime = System.currentTimeMillis()
    numbers.reverse
    println( "Built-in reverse: " + ( System.currentTimeMillis() - startTime ) + " milliseconds" )

    benchmark[Int]( "Recursive reverse", reverseRecursive[Int], numbers )
    benchmark[Int]( "Tail recursive reverse", reverseTailRecursive[Int], numbers )
    benchmark[Int]( "Functional reverse", reverseFunctional[Int], numbers )

  }

}
