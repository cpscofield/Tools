import java.io.File;
import scala.collection.mutable.ArrayBuffer;

object DirSize {

    var SHOW_FILES : Boolean = false;
    var DEBUGGING : Boolean = false;
    var FILE_TYPES : Array[String]=Array.fill(10)("*");
    var ALL_FILES : Boolean = true;
   

    def subdirs(dir: File): Iterator[File] = {
        val children : Array[File] = dir.listFiles.filter(_.isDirectory);
        children.toIterator ++ children.toIterator.flatMap(subdirs _);
    }

    def wantedFileType( path : String ) : Boolean = {
        if( ALL_FILES ) true;
        else {
            for( i <- 0 until FILE_TYPES.length ) {
                if( FILE_TYPES(i) != "*" ) {
                    return path.endsWith( FILE_TYPES( i ) );
                }
            }
        }
        false
    }
    
    def dirfiles(dir: File): Iterator[File] = {
        //println( "\tdir=" + dir );
        var children : Array[File] = dir.listFiles() filter( x => wantedFileType( x.getPath() ) );
        if( children == null ) children = new Array[File](0);
        children.toIterator;
    }

    // def listfiles( files: Iterator[File] ) : String = {
    //     var filelist : StringBuilder = new StringBuilder();
    //     for( f <- files ) filelist.append( f ).append( "," );
    //     filelist.toString();
    // }

    def getAttributes( f: File ) : String = {
        var attribs : StringBuilder = new StringBuilder();
        if( f.isDirectory() ) attribs.append( "directory " );
        else if( f.isFile() ) attribs.append( "file " );
        else if( f.isHidden() ) attribs.append( "hidden " );
        else if( f.getName().endsWith( ".lnk" ) ) attribs.append( "link " );
        if( !f.isAbsolute() ) attribs.append( "absolute=no " );
        attribs.append( "length=" + f.length() );
        attribs.toString();
    }

    def getSize( f: File ) : Long = {
        if( DEBUGGING ) println( "\tDEBUG: " + f.getPath() + " " + getAttributes( f ) );
        var size : Long = 0;
        var dirsize : Long = 0;
        if( f.isDirectory() ) {
            var files = new ArrayBuffer[File]();
            for( d <- dirfiles( f ) ) {
                size = getSize( d );
                dirsize += size;
                //println( d.getPath() + " has " + size + " bytes" );
                if( SHOW_FILES && !d.isDirectory() ) println( "\t" + size + ": " + d.getPath() );
            }
            println( dirsize + ": " + f.getPath() + "\\" );
        }
        else if( f.getPath().endsWith( ".lnk" ) ) {
            //println( "\tLINK: " + f.getPath() );
            // ignore
        }
        else {

            dirsize = f.length();
            // println( f.getPath() + " has " + size + " bytes" );
        }
        dirsize;
    }

    def main( args: Array[String]) {
        var rootdir : String = "";
        var files = new ArrayBuffer[File]();
        var fileTypeArgs : Boolean = false;
        for( i <- 0 to args.length-1 ) {
            if( args(i) == "--files" ) SHOW_FILES = true;
            else if( args(i) == "--debug" ) DEBUGGING = true;
            else if( args(i) == "--types" ) fileTypeArgs = true;
            else if( fileTypeArgs ) { FILE_TYPES = args(i).split('|'); ALL_FILES = false; }
            else rootdir = args(i);
        }
        // for( d <- dirfiles(new File(rootdir))) files += d;
        // //println( files );
        // var size: Long = 0;
        // var f: File = null;
        // for( f <- files ) size += getSize( f );

        var size : Long = 0;
        size = getSize( new File(rootdir) );

        println( size + ": " + rootdir.replace( '/', '\\' ) + "\\" );
    }
}
