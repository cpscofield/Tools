// This tool is based on the code from the Java tutorial "Walking the File Tree"
// from http://docs.oracle.com/javase/tutorial/essential/io/walk.html with the 
// additional function of tracking and printing file sizes and accumulating 
// directory sizes.
package vycegripp;

import java.util.HashMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import static java.nio.file.FileVisitResult.*;

public final class DirLister
        extends SimpleFileVisitor<Path> {

    private HashMap<String, Long> dirSizes = new HashMap<>();
    private static final String FILE_SEP = System.getProperty("file.separator");
    private static long numFiles = 0;
    private static long numDirectories = 0;
    private static final boolean DEBUGGING = false;
    private static long startTime = 0;

    public static void main(String[] args) {
        try {
            DirLister dl = new DirLister();
            Path file = Paths.get(args[0]);
            startTime = System.currentTimeMillis();
            Path tree = Files.walkFileTree(file, dl);
            System.out.format("# files=%d # directories=%d%n", numFiles, numDirectories);
            System.out.format("Total time=%f seconds %n", (double)(System.currentTimeMillis()-startTime)/1000.0D);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

    /** Print information about each type of file. Gets the size of the file
     * and determines the containing directory and adds the size into
     * the directory's size accumulation.
     * 
     * @param file File information.
     * @param attr File attributes
     * @return FileVisitResult: always CONTINUE
     * @throws IOException 
     */
    @Override
    public FileVisitResult visitFile(Path file,
            BasicFileAttributes attr) throws IOException {
        if (attr.isSymbolicLink()) {
            System.out.format("Symbolic link: %s ", file);
        } else if (attr.isRegularFile()) {
            debug( "visitFile=%s%n", file.toString() );
            ++numFiles;
            int index = file.toString().lastIndexOf(FILE_SEP);
            String parentName = file.toString().substring(0, index);
            System.out.format(" f %9d %s%n", attr.size(), file);
            Long accumDirSize = dirSizes.get(parentName);
            accumDirSize += attr.size();
            debug("%s size is now %d bytes%n", parentName, accumDirSize );
            dirSizes.put(parentName, accumDirSize);
        } else if (attr.isDirectory()) {
//            debug( "File is a DIRECTORY:%s%n", file.toString() );
//            FileVisitResult result = postVisitDirectory(file, null);
        } else {
            System.out.format("Other: %s ", file);
        }
        return CONTINUE;
    }

    /** Print each directory visited.
     *
     * @param dir The directory path
     * @param exc IOException 
     * @return FileVisitResult: always CONTINUE
     * @throws IOException 
     */
    @Override
    public FileVisitResult postVisitDirectory(Path dir,
            IOException exc) throws IOException {
        debug( "postVisitDirectory=%s%n", dir.toString() );
        ++numDirectories;
        int index = dir.toString().lastIndexOf(FILE_SEP);
        String parentName = dir.toString().substring(0, index);
        Long dirSize = dirSizes.get(dir.toString());
        Long accumDirSize = dirSizes.get(parentName);
        debug("accumDirSize=%d dirSize=%d%n", accumDirSize, dirSize);
        System.out.format("d %10d %s%n", dirSize.longValue(), dir);
        if( accumDirSize == null ) accumDirSize = 0L;
        accumDirSize += dirSize;
        dirSizes.put(parentName,accumDirSize );
        debug("Removing directory %s%n", dir.toString());
        dirSizes.remove(dir.toString()); // We no longer need this directory data
        return CONTINUE;
    }

    /** Start accumulator for newly visited directory.
     * 
     * @param dir
     * @param attr
     * @return
     * @throws IOException 
     */
    @Override
    public FileVisitResult preVisitDirectory(Path dir,
            BasicFileAttributes attr) throws IOException {
        debug( "preVisitDirectory=%s%n", dir.toString() );
        dirSizes.put(dir.toString(), 0L);
        return CONTINUE;
    }

    // If there is some error accessing
    // the file, let the user know.
    // If you don't override this method
    // and an error occurs, an IOException 
    // is thrown.
    @Override
    public FileVisitResult visitFileFailed(Path file,
            IOException exc) {
        System.err.println(exc);
        return CONTINUE;
    }
    
    private static void debug( final String format, final Object... args ) {
        if( DEBUGGING ) System.out.format( "DEBUG: " + format, args );
    }
}
