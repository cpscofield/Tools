/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ods

object StackTest {

  def main(args: Array[String]): Unit = {

    var s = new Stack[String]
    s.push( "A String" )
    s.push( "2nd String" )
    s.push( "Another String" )
    println( "s=" + s )
    if( s.peek == "Another String" ) println( "OK!" ); else println( "Not OK!" )
    println( s.pop )
    if( s.peek == "2nd String" ) println( "OK!" ); else println( "Not OK!" )
    println( s.pop )
    if( s.peek == "A String" ) println( "OK!" ); else println( "Not OK!" )
    println( s.pop )
    if( s.peek == null ) println( "OK!" ); else println( "Not OK!" )

    var s2 = new Stack[Any]
    s2.push( List(1,1,2,3,5,8))
    if( s2.peek == List(1,1,2,3,5,8) ) println( "OK!" ); else println( "Not OK!" )
    s2.push( Set(1,1,2,3,4) )
    if( s2.peek == Set(4,3,2,1) ) println( "OK!" ); else println( "Not OK!" )
    s2.push( 3.14159 )
    s2.push( new java.math.BigInteger( "258000000000000000" ) )
    if( s2.contains( 3.14159) ) println( "OK!" ); else println( "Not OK!" )
    println( "s2=" + s2 )
    val s3 = s2.cloneIt
    println( "s3=" + s3 )
    if( s2 == s3 ) println( "OK!" ); else println( "Not OK!" )
    s2.pop()
    if( s2 != s3 ) println( "OK!" ); else println( "Not OK!" )
    s2.clear()
    if( s2.peek == null ) println( "OK!" ); else println( "Not OK!" )
    if( s2.size == 0 ) println( "OK!" ); else println( "Not OK!" )
    if( s2.isEmpty )  println( "OK!" ); else println( "Not OK!" )
    if( s3.size > 0 ) println( "OK!" ); else println( "Not OK!" )
    if( s3.peek.asInstanceOf[java.math.BigInteger].doubleValue() == 2.58E17 )
        println( "OK!" ); else println( "Not OK!" )

  }

}
