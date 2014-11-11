
package ods

import scala.collection.mutable.ArrayBuffer

object Stack {
  def apply[T] () = new Stack
}

/**
 * Simple implementation of a Stack data structure.
 * (Just experimenting with Scala in full realization that
 * there is already defined a Stack class in the Scala collection!)
 */
class Stack[T] extends Structure[T] {
  val stack : ArrayBuffer[T] = new ArrayBuffer[T]()
  def push( elem: T ) {
    stack += elem
  }
  def ++( elem: T ) {
    push( elem )
  }
  def pop() : T = {
    if( stack.isEmpty ) null.asInstanceOf[T]
    else stack.remove( stack.length - 1 )
  }
  def --() : T = {
    pop()
  }
  def peek : T = {
    if( stack.isEmpty ) null.asInstanceOf[T]
    else stack.last
  }
  def ==( that : Stack[T] ) : Boolean = {
    if( that == null.asInstanceOf[ Any ] ) return false
    if( this.size != that.size ) return false
    for( i <- 0 to this.size-1 ) if( this.stack(i) != that.stack(i) ) return false
    true
  }
  def !=( that: Stack[T] ) : Boolean = {
    !(this.==( that ) )
  }
  override def toString : String = {
    stack.mkString("{",",","}")
  }
  //------------------------------------
  // Structure[T] overrides:
  def size : Int = stack.length
  def isEmpty : Boolean = stack.isEmpty
  def clear() : Unit = stack.clear
  def contains( elem : T ) : Boolean = stack.indexOf( elem ) != -1
  def add( elem : T ) : Unit = this.push( elem )
  def remove( elem : T ) : T = null.asInstanceOf[T] // irrelevant in this context?
  def iterator : Iterator[T] = stack.toIterator
  def collection : java.util.Collection[T] = {
    val javaStack = new java.util.Stack[T]()
    val iter = stack.iterator
    while( iter.hasNext ) javaStack.push( iter.next )
    javaStack
  }
  def cloneIt() : Stack[T] = {
    val newStack = new Stack[T]
    val iter = stack.iterator
    while( iter.hasNext ) newStack.push( iter.next )
    newStack
  }
}
