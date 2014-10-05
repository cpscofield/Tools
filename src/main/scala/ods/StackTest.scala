/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ods

object StackTest {

  def assertTrue( expression : Boolean, msgIfNotTrue : String = "Not OK!" ) : Boolean = {
    if( expression ) println( "OK!" )
    else println( s"Assertion failure!: $msgIfNotTrue" )
    expression
  }

  def main(args: Array[String]): Unit = {

    var s = new Stack[String]
    s.push( "A String" )
    s.push( "2nd String" )
    s.push( "Another String" )
    println( "s=" + s )
    assertTrue( s.peek == "Another String",  """s.peek not equal to "Another String" """ )
    println( s.pop )
    assertTrue( s.peek == "2nd String", """s.peek not equal to "2nd String" """ )
    println( s.pop )
    assertTrue( s.peek == "A String", """s.peek not equal to "A String" """ )
    println( s.pop )
    assertTrue( s.peek == null, """s.peek not equal to null""" )

    var s2 = new Stack[Any]
    s2.push( List(1,1,2,3,5,8))
    assertTrue( s2.peek == List(1,1,2,3,5,8) )
    s2.push( Set(1,1,2,3,4) )
    assertTrue( s2.peek == Set(4,3,2,1) )
    s2.push( 3.14159 )
    s2.push( new java.math.BigInteger( "258000000000000000" ) )
    assertTrue( s2.contains( 3.14159) )
    println( "s2=" + s2 )
    val s3 = s2.cloneIt
    println( "s3=" + s3 )
    assertTrue( s2 == s3 )
    s2.pop()
    assertTrue( s2 != s3 )
    assertTrue( s2 != null )
    s2.clear()
    assertTrue( s2.peek == null )
    assertTrue( s2.size == 0 )
    assertTrue( s2.isEmpty )
    assertTrue( s3.size > 0 )
    assertTrue( s3.peek.asInstanceOf[java.math.BigInteger].doubleValue() == 2.58E17 )


  }

}
