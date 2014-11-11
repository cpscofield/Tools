package ods

/**
 * Scala version of Duane Bailey's Structure interface from "Java Structures".
 */
trait Structure[T] {
  def size : Int
  // post: computes number of elements contained in structure

  def isEmpty : Boolean
  // post: return true iff the structure is empty

  def clear() : Unit
  // post: the structure is empty

  def contains( element : T ) : Boolean
  // pre: element is non-null
  // post: returns true iff element.equals some element in structure

  def add( element : T ) : Unit
  // pre: element is non-null
  // post: element has been added to the structure
  //       replacement policy is not specified

  def remove( element : T ) : T
  // pre: element is non-null
  // post: an object equal to element is removed and returned, if found

  def iterator : Iterator[T]
  // post: returns an iterator for traversing structure;
  //       all structure package implementations return
  //       an AbstractIterator

  def collection : java.util.Collection[T]
  // post: returns a Collection that may be used with
  //       Java's Collection Framework

  //--- Added (i.e., not from original Structure interface definition)
  def cloneIt : Structure[T]
  // post: returns a copy of the Structure
  // Note:  called 'cloneIt' instead of 'clone' to avoid conflict with java.lang.Object.clone()
}
