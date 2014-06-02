/**
 * Simple stack implementation; just playing around with Scala. :)
 */

package vycegripp

import scala.collection.mutable.ArrayBuffer

object Stack {

  def main(args: Array[String]): Unit = {
    var s = new Stack[String]
    s.push( "A String" )
    s.push( "2nd String" )
    s.push( "Another String" )
    println( "s=" + s )
    if( s.peek() == "Another String" ) println( "OK!" ); else println( "Not OK!" )
    println( s.pop )
    if( s.peek() == "2nd String" ) println( "OK!" ); else println( "Not OK!" )
    println( s.pop )
    if( s.peek() == "A String" ) println( "OK!" ); else println( "Not OK!" )
    println( s.pop )
    if( s.peek() == null ) println( "OK!" ); else println( "Not OK!" )

    var s2 = new Stack[Any]
    s2.push( List(1,1,2,3,5,8))
    if( s2.peek() == List(1,1,2,3,5,8) ) println( "OK!" ); else println( "Not OK!" )
    s2.push( Set(1,1,2,3,4) )
    if( s2.peek() == Set(1,2,3,4) ) println( "OK!" ); else println( "Not OK!" )
    s2.push( 3.14159 )
    s2.push( new java.math.BigInteger( "258000000000000000" ) )
    println( "s2=" + s2 )
    for( i <- 0 to s2.size() - 1  ) s2.pop
    if( s2.peek() == null ) println( "OK!" ); else println( "Not OK!" )
    if( s2.size() == 0 ) println( "OK!" ); else println( "Not OK!" )
  }

}

class Stack[T] {
  val stack = ArrayBuffer[T]()
  def push( elem: T ) {
    stack += elem
  }
  def pop() : T = {
    if( stack.length == 0 ) null.asInstanceOf[T]
    else stack.remove( stack.length - 1 )
  }
  def peek() : T = {
    if( stack.length == 0 ) null.asInstanceOf[T]
    else stack.last
  }
  def size() = stack.length
  override def toString() : String = {
    stack.mkString(",")
  }
}