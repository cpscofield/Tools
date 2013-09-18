// =============================================================================
// LinkCheckerMT by Cary Scofield (carys689@gmail.com) is licensed under 
// a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
// (See http://www.creativecommons.org for details).
//
// RECIPIENT ACCEPTS THE GOODS “AS IS,” WITH NO REPRESENTATION OR WARRANTY 
// OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION IMPLIED 
// WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
// =============================================================================

package vycegripp;

import java.io.InputStream;
import java.io.IOException;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.CharConversionException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.NoSuchElementException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * This program checks the http links found in a web page. Program takes
 * one argument which is the path of the properties file. In the properties
 * file, there must be a property named "URL" pointing to the web page to
 * test the links on. When program is complete, if there are any "bad links",
 * a list of the bad links will be produced.
 * </p>
 * <p>
 * Sample output extract:
 * <br/>
 * <p>
 * The following is the partial output if you run the tool against 
 * http://en.wikipedia.org/wiki/MapReduce :
 * </p>
 * <pre>
 * Bad link: http://graal.ens-lyon.fr/mapreduce/
 * Bad link: http://patft.uspto.gov/netacgi/nph-Parser?Sect1=PTO1&amp;Sect2=HITOFF&amp;d=PALL&amp;p=1&amp;u=/netahtml/PTO/srchnum.htm&amp;r=1&amp;f=G&amp;l=50&amp;s1=7,650,331.PN.&amp;OS=PN/7,650,331&amp;RS=PN/7,650,331
 * Bad link: http://www.cse.ust.hk/catalac/users/saven/GPGPU/MapReduce/PACT08/171.pdf
 * Bad link: http://ppi.fudan.edu.cn/_media/publications;ostrich-pact10.pdf?id=rong_chen&amp;cache=cache
 * Finished testing all links.
 * Total time: 12.3730 seconds.
 * Total number of links checked: 73
 * Total number of bad links found: 4
 * Avg. time per link check: 0.169493 seconds.
 * </pre>
 * </p>
 * 
 * @since 1.5
 * @author Cary Scofield (carys689@gmail.com)
 * @date November/December 2012
 */
public final class LinkCheckerMT {

    public static void main( String[] args ) {
        try {
            if (args.length == 0) {
                throw new IllegalArgumentException("Missing URL from command line");
            }
            LinkCheckerMT lc = new LinkCheckerMT();
            lc.execute( lc.getProperties( args[0] ).getProperty( "URL" ) );
        }
        catch( Exception e ) {
            e.printStackTrace();
            System.exit( 1 );
        }
        System.exit( 0 );
    }

    public final static int     MAX_THREADS = 4; // On a dual-core machine, this is plenty
    public final static boolean TRACING_CHAR_INPUT = false;
    public final static boolean TRACING_URLS = true;
    public final static double  MILLISECS_PER_SECOND = 1000.0D;
    public final static String  DEFAULT_ENCODING = "UTF-8";
    public final static int     WAIT_FOR_COMPLETION_IN_SECONDS = 60;

    // A distinguishable exception class to indicate that a "bad" link was found.
    static final class BadLinkException extends Exception {
        public BadLinkException() { super(); }
        public BadLinkException( String msg ) { super( msg ); }
        public BadLinkException( String msg, Throwable cause ) { super( msg, cause ); }
    }

    // Thread-safe structures to keep track of links to be tested and bad links that are found.
    private final ConcurrentLinkedQueue<String> linksToTest = new ConcurrentLinkedQueue<String>();
    private final ConcurrentLinkedQueue<String> badLinks    = new ConcurrentLinkedQueue<String>();

    public ConcurrentLinkedQueue<String> getLinksToTest() {
        return this.linksToTest;
    }
    public ConcurrentLinkedQueue<String> getBadLinks() {
        return this.badLinks;
    }

    private ExecutorService execService = null;

    public Properties getProperties( String propertiesPath ) throws Exception {
        File file = new File( propertiesPath );
        FileInputStream fis = new FileInputStream( file );
        Properties props = new Properties();
        props.load( fis );
        return props;
    }

   /**
     * Execute the link checker.
     * @param url: The URL of the page whose links we are going to check.
     * @throws Exception
     */
    public void execute( String url ) throws Exception {
        final long startTime = System.currentTimeMillis();
        long endTime = startTime;
        try {
            InputStream is = openURL(new URI(url));
            try {
                // Load up the linksToTest object.
                this.getLinksFromPage( is );
            } catch (EOFException e) {
                this.setLinkCount( linksToTest.size() );
                System.out.println( this.getLinkCount() + " link(s) found. Proceeding with connectivity tests.");
            }

            // Kick off the link checker threads and wait for completion of all tasks.
            execService = this.launchLinkCheckers( MAX_THREADS );
            boolean completed = this.waitForCompletion( execService );
            if( !completed ) {
                System.out.println( "Warning: not all tasks finished." );
                this.cancelLinkChecker();
            }
            
            this.setBadLinkCount( this.badLinks.size() );

            for( int i = 0; i < this.getBadLinkCount(); ++i ) {
                String badLink = this.badLinks.poll();
                if( badLink == null ) break;
                System.out.println( "Bad link: " + badLink );
            }

            if( this.linksToTest.size() > 0 ) {
                System.out.println( this.linksToTest.size() + " links untested!!" );
            }
            else {
                System.out.println( "Finished testing all links." );
            }
        }        
        catch( Exception e ) {
            throw e;
        }
        
        endTime = System.currentTimeMillis();
        final double seconds = ((double)( endTime - startTime))/MILLISECS_PER_SECOND;
        System.out.format( "Total time: %g seconds.%n", seconds );
        System.out.format( "Total number of links checked: %d%n", this.getLinksTested() );
        System.out.format( "Total number of bad links found: %d%n", this.getBadLinkCount() );
        if( this.getLinksTested() > 0 ) System.out.format( "Avg. time per link check: %g seconds.%n", seconds/this.getLinksTested() );
        
    }

    /**
     * Cancel execution of the threads immediately (if possible)
     * @param es
     * @throws Exception
     */
    public void cancelLinkChecker() throws Exception {
        this.execService.shutdownNow();
    }

    /**
     * Get all HTTP links from web page. Results are placed in linksToTest.
     * @param is Web page is opened on this stream.
     * @throws EOFException End-of-file reached on web page.
     * @throws Exception
     */
    private void getLinksFromPage(InputStream is) throws Exception {
        try {
            while (true) {
                String linkValue = getNextLinkValue(is);
                if (linkValue == null) {
                    break;
                }
                //System.out.println("Adding to list of links to test: " + linkValue);
                if (isHttpLink(linkValue)) {
                    linksToTest.add(linkValue);
                }
            }
        } catch (EOFException e) {
            throw e;
        }

    }

    // Number of links where connectivity failed. Not thread safe and not shared.
    private int badLinkCount = 0;
    public int getBadLinkCount() {
        return this.badLinkCount;
    }
    private void setBadLinkCount( final int count ) {
        this.badLinkCount = count;
    }

    // Number of links that will be tested. Not thread safe and not shared.
    private int linkCount = 0;
    public int getLinkCount() {
        return this.linkCount;
    }
    private void setLinkCount( final int count ) {
        this.linkCount = count;
    }

    // Number of links actually tested; ideally, should be equal to linkCount
    // when the program is complete. Thread safe and shared.
    private AtomicInteger linksTested = new AtomicInteger(0);
    public int getLinksTested() {
        return this.linksTested.get();
    }
    public int incrementLinksTested() {
        return this.linksTested.getAndIncrement();
    }

    /**
     * Spawn the threads that will test the link connectivity.
     * @param numTasks
     * @return ExecutorService holding the thread pool.
     * @throws Exception
     */
    public ExecutorService launchLinkCheckers(int numTasks) throws Exception {
        final int START_INTERVAL = 500;
        ExecutorService es = Executors.newFixedThreadPool(numTasks);
        for (int i = 0; i < numTasks; ++i) {
            es.execute(new LinkCheckerRunnable());
            try {
                Thread.sleep(START_INTERVAL);
            } catch (InterruptedException e) {
                // Do nothing
            }
        }
        return es;
    }

    /**
     * Wait for all the threads to complete.
     * @param es
     * @return <tt>true</tt> if all threads completed; otherwise, return <tt>false</tt>.
     * @throws Exception
     */
    public boolean waitForCompletion( ExecutorService es ) throws Exception {
        es.shutdown();
        boolean done = es.awaitTermination(WAIT_FOR_COMPLETION_IN_SECONDS, TimeUnit.SECONDS);
        System.out.println( "Shutdown complete?: " + (done?"yes":"no") );
        return done;
    }
    
    /**
     * Runnable task that tests HTTP links found in a web page. Contains
     * a loop that first removes a link value from the queue, tests the
     * connectivity of the link and if the connectivity fails, adds the
     * link value to list of bad links. Task is finished when the link
     * queue is empty.
     */
    class LinkCheckerRunnable implements Runnable {

        private String linkToTest = null;

        @Override
        public void run() {
            System.out.println( Thread.currentThread().getName() + " thread starting..." );
            LOOP: while ( true ) {

                try {
                    // Remove a link from the queue...
                    linkToTest = linksToTest.remove();
                    System.out.println( linksToTest.size() + " links remaining to be tested.");
                } catch (NoSuchElementException e) {
                    // No more links to test
                    break LOOP;
                }
                catch( Throwable t ) {
                    System.err.println( t.toString() );
                    break LOOP;
                }
 
                try {
                    try {
                        // Test the connectivity to the link. If it
                        // fails, add link to bad link queue.
                        incrementLinksTested();
                        if (!checkLink(new URI(linkToTest))) {
                            throw new BadLinkException(linkToTest);
                        }
                    } catch (BadLinkException e) {
                        badLinks.add(URLDecoder.decode(linkToTest, DEFAULT_ENCODING));
                        System.err.println(e.toString());
                    } catch (Exception e) {
                        badLinks.add(URLDecoder.decode(linkToTest, DEFAULT_ENCODING));
                        System.err.println(e.toString());
                    }
                } catch (java.io.UnsupportedEncodingException e) {
                    System.err.println(e.toString());
                    break LOOP;
                } catch (Exception e ) {
                    System.err.println(e.toString());
                    break LOOP;
                }

                Thread.yield(); // Let someone else have a turn...

            } // end while
            System.out.println( Thread.currentThread().getName() + " thread exiting..." );
        } // end run()
    }

    /**
     * Check to see if we can connect to the link.
     * @param uri: The link we are going to check.
     * @return <tt>true</tt> if link is okay; otherwise <tt>false</tt>.
     * @throws BadLinkException If unable to connect to the site.
     */
    private static boolean checkLink( final URI uri ) throws BadLinkException {
        if( uri == null ) throw new IllegalArgumentException( "uri is null" );
        boolean valid = false;
        try {
            URL url = uri.toURL();
            if( TRACING_URLS) System.out.println( Thread.currentThread().getName() + " Checking: " + url.toString() );
            InputStream is = url.openStream();
            if( is != null ) {
                valid = true;
                is.close();
            }
            return valid;
        }
        catch( Exception e ) {
            if( uri.toString().indexOf( "finance.yahoo.com" ) != -1 ) e.printStackTrace();
            throw new BadLinkException( "\t" + uri.toString(), e );
        }
    }

    /**
     * Open a stream to the URL.
     * @param uri: The URI to open.
     * @return InputStream, in particular a BufferedInputStream
     * @throws Exception 
     */
    private static InputStream openURL( final URI uri ) throws Exception {
        if( uri == null ) throw new IllegalArgumentException( "uri is null" );
        final URL url = uri.toURL();
        final InputStream is = url.openStream();
        final BufferedInputStream bis = new BufferedInputStream( is, 4096 );
        return bis;
    }

    /**
     * Read a character from the InputStream.
     * @param is: InputStream
     * @return char: Character read from input.
     * @throws CharConversionException: If illegal character found in input.
     * @throws EOFException: If end-of-file reached
     * @throws IOException: On any other I/O error.
    */
    private static char readChar( final InputStream is ) throws IOException, EOFException, CharConversionException {
        final int END_OF_FILE = -1;
        final int MAX_CHAR_VALUE = 0xff;
        final int ch = is.read();
        if( ch == END_OF_FILE ) throw new EOFException();
        if( ch > MAX_CHAR_VALUE ) throw new CharConversionException();
        if( TRACING_CHAR_INPUT ) System.err.print( (char)ch );
        return (char)ch;
    }

    /**
     * Read a character from the InputStream, skipping any whitespace.
     * @param is: InputStream
     * @param skipWhitespace: Tells method whether or not to skip whitespace.
     * @return char: Character read from input.
     * @throws CharConversionException: If illegal character found in input.
     * @throws EOFException: If end-of-file reached
     * @throws IOException: On any other I/O error.
    */
    private static char readChar( final InputStream is, final boolean skipWhitespace ) throws IOException, EOFException, CharConversionException {
        int ch = readChar( is );
        if( skipWhitespace ) {
            while (Character.isWhitespace(ch)) {
                ch = readChar(is);
            }
        }
        return (char)ch;
    }
    
    /**
     * Skip HTML comment if found.
     * @param is: InputStream
     * @return Next character in input if no comment found; otherwise, character immediately following comment.
     * @throws Exception 
     */
    private static char skipComment(final InputStream is) throws Exception {
        char ch = readChar(is);
        if (ch == '<') {
            ch = readChar(is);
            if (ch == '!') {
                ch = readChar(is);
                if (ch == '-') {
                    ch = readChar(is);
                    if (ch == '-') {
                        ch = skipRestOfComment(is);
                    }
                }
            }
        }
        return ch;
    }

    /**
     * Skip rest of comment in input.
     * @param is: InputStream
     * @return Character immediately following the end of the comment.
     * @throws Exception 
     */
    private static char skipRestOfComment( final InputStream is ) throws Exception {
        char ch = ' ';
        while( true ) {
            ch = readChar( is );
            if( ch != '-' ) continue;
            ch = readChar( is );
            if( ch != '-' ) continue;
            ch = readChar( is );
            if( ch != '>' ) continue;
            // end of HTML comment found
            ch = readChar( is );
            break; 
        }
        return ch;
    }
    
    /**
     * Elicit the next link value as a string from the page source. Assumption:
     * the web page we are scanning is syntactically correct and the InputStream
     * object knows the current position in the page.
     * 
     * @param is: InputStream
     * @return The value of the link attribute. 
     *      For &lt;A&gt; element, it will be the HREF attribute. 
     *      For the &lt;IMG&gt; element, it will be the SRC attribute.
     * @throws EOFException When end-of-file encountered.
     * @throws Exception For other exception.
     */
    private static String getNextLinkValue( final InputStream is ) throws EOFException, Exception {
        final boolean SKIPWS = true; // Skip whitepace
        final StringBuilder buf = new StringBuilder( 256 );
        while( true ) {
            try {
                char ch = skipComment( is ); 
                if( ch != 'h' && ch != 'H' ) {
                    // See if this might be a link to an image file...
                    if( ch != 's' && ch != 'S' ) continue;
                    ch = readChar( is ); if( ch != 'r' && ch != 'R' ) continue;
                    ch = readChar( is ); if( ch != 'c' && ch != 'C' ) continue;
                    ch = readChar( is, SKIPWS ); if( ch != '=' ) continue;
                    ch = readChar( is, SKIPWS ); if( ch != '"' ) continue;
                    while ((ch = readChar(is)) != '"') {
                        buf.append(ch);
                    }
                    break;
                }
                ch = readChar( is ); if( ch != 'r' && ch != 'R' ) continue;
                ch = readChar( is ); if( ch != 'e' && ch != 'E' ) continue;
                ch = readChar( is ); if( ch != 'f' && ch != 'F' ) continue;
                ch = readChar( is, SKIPWS ); if( ch != '=' ) continue;
                ch = readChar( is, SKIPWS ); if( ch != '"' ) continue;
                while ((ch = readChar( is )) != '"') {
                    buf.append(ch);
                }
            } catch (EOFException e) {
                if( buf.length() == 0 ) throw e;
            } catch (Exception e) {
                System.err.println( e.toString() );
                System.err.println( buf.toString() );
                throw e;
            } 
        
            break;
        }

        if (buf.length() == 0) {
            return null;
        } else {
            return encodeQueryString( buf.toString() );
        }
    }

    /**
     * Encode the query portion of the link if it exists.
     * @param link
     * @return Properly encoded link.
     * @throws Exception
     */
    private static String encodeQueryString( final String link) throws Exception {
        final int beginQuery = link.indexOf("?");
        final boolean queryPresent = beginQuery != -1;
        if (queryPresent) {
            final StringBuilder revisedLink = new StringBuilder();
            revisedLink.append( link.substring( 0, beginQuery+1 ) );
            revisedLink.append( URLEncoder.encode( link.substring( beginQuery+1 ), DEFAULT_ENCODING ) );
            return revisedLink.toString();
        } else {
            return link;
        }
    }

    /**
     * Check to see if link begins with the string "http".
     * @param linkValue: String containing the URL.
     * @return <tt>true</tt> if "http" found; otherwise, return <tt>false</tt>.
     * @throws Exception 
     */
    private static boolean isHttpLink( final String linkValue ) throws Exception {
        return linkValue.toLowerCase().startsWith( "http" );
    }
    
}
