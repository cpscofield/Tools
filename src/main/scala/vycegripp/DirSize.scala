package vycegripp

import java.io.File

/**
 * This program will traverse a directory tree at a given starting point and produce
 * a listing of files and directories along with their sizes.
 */
object DirSize {

    var SHOW_FILES : Boolean = false
    var numFiles : Long = 0
    var numDirectories : Long = 0
    var startTime : Long = 0

    /**
     * Up to two arguments can be passed: 1) if '--files' given, print the names/sizes of the files;
     * otherwise just print the names/sizes of the directories; 2) the root directory's name for
     * directory traversal starting point.
     */
    def main( args: Array[String]) {
        var rootdir : String = ""
        var fileTypeArgs : Boolean = false
        for( i <- 0 to args.length-1 ) {
            if( args(i) == "--files" ) SHOW_FILES = true
            else rootdir = args(i)
        }

        var size : Long = 0
        startTime = System.currentTimeMillis
        size = getSize( new File(rootdir) )

        println( "# files=%d # directories=%d".format( numFiles, numDirectories ) )
        println( "Total time=%f seconds".format( (System.currentTimeMillis() - startTime ).asInstanceOf[Double]/1000.0D ) )
    }

    /**
     * Get the elements of a directory.
     */
    def dirfiles(dir: File): Iterator[File] = {
        var children : Array[File] = dir.listFiles()
        if( children == null ) children = new Array[File](0)
        children.toIterator
    }

    /**
     * Get the attributes of a file (for debugging purposes).
     */
    def getAttributes( f: File ) : String = {
        var attribs : StringBuilder = new StringBuilder()
        if( f.isDirectory() ) attribs.append( "directory " )
        else if( f.isFile() ) attribs.append( "file " )
        else if( f.isHidden() ) attribs.append( "hidden " )
        else if( f.getName().endsWith( ".lnk" ) ) attribs.append( "link " )
        if( !f.isAbsolute() ) attribs.append( "absolute=no " )
        attribs.append( "length=" + f.length() )
        attribs.append( " name=" + f.getName() )
        attribs.toString()
    }

    /**
     * Depth-first recursive traversal of the directory tree printing names and sizes of the
     * elements (i.e., directories and files) and accumulating the sizes of the directories.
     */
    def getSize( f: File ) : Long = {
        //println( getAttributes( f ) )
        var size : Long = 0
        var dirsize : Long = 0
        if( f.isDirectory() ) {
            for( d <- dirfiles( f ) ) {
                size = getSize( d )
                dirsize += size
                if( !d.isDirectory() ) numFiles += 1
                if( SHOW_FILES && !d.isDirectory() ) println( " f %9d %s".format( size, d.getPath() ) )
            }
            numDirectories += 1
            println( "d %10d %s".format( dirsize, f.getPath() ) )
        }
        else {
            dirsize = f.length()
        }
        dirsize
    }

}
