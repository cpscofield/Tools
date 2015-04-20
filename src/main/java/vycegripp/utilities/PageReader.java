// =============================================================================
// PageReader by Cary Scofield (carys689@gmail.com) is licensed under a 
// Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
// (See http://www.creativecommons.org for details).
//
// RECIPIENT ACCEPTS THE GOODS “AS IS,” WITH NO REPRESENTATION OR WARRANTY 
// OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION IMPLIED 
// WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
// =============================================================================

package vycegripp.utilities;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

/**
 * <p>
 * This class reads a page (or file) as identified by the URI.
 * </p>
 * <p>
 * There are two ways to use this class: one is by making a static call to
 * PageReader.readPage() in which case the client manages the data in its own
 * object space. The other is by creating a PageReader object and accessing the
 * data through the getPage() method, in which case the PageReader object keeps
 * the data in its own space.
 * </p>
 */
public class PageReader {

    private long time = 0;
    private StringBuilder page = null;
    private String uriPath = null;

    public PageReader(String uriPath) throws Exception {
        this.uriPath = uriPath;
        this.page = new StringBuilder();
        try {
            this._readPage(new URI(uriPath));
        } catch (Exception e) {
            System.err.println(e.toString() + " on " + uriPath);
            throw e;
        }
    }

    public StringBuilder getPage() throws Exception {
        this.page = new StringBuilder();
        try {
            this._readPage(new URI(this.uriPath));
        } catch (Exception e) {
            System.err.println(e.toString() + " on " + uriPath);
            throw e;
        }
        return this.page;
    }

    public long getTime() {
        return this.time;
    }

    private void _readPage(URI uri) throws Exception {
        InputStream is = null;
        try {
            long startTime = System.currentTimeMillis();
            is = openURI(uri);
            StringBuilder buf = this.page;
            final int END_OF_FILE = -1;
            int ch;
            while ((ch = is.read()) != END_OF_FILE) {
                buf.append((char) ch);
            }
            long endTime = System.currentTimeMillis();
            this.time = endTime - startTime;
        } catch (Exception e) {
            System.err.println(e.toString());
            throw e;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * Read the page from the input stream.
     *
     * @param uri
     * @return A CharSequence object containing contents read from input stream.
     * @throws Exception
     */
    public static StringBuilder readPage(URI uri) throws Exception {
        InputStream is = null;
        StringBuilder page = null;
        try {
            is = openURI(uri);
            page = new StringBuilder();
            final int END_OF_FILE = -1;
            int ch;
            while ((ch = is.read()) != END_OF_FILE) {
                page.append((char) ch);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (is != null) {
                is.close();
            } else {
                throw new java.io.IOException("Unable to open URI: " + uri);
            }
            return page;
        }
    }

    /**
     * Open the input stream and return it.
     *
     * @param uri
     * @return the input stream.
     * @throws Exception
     */
    private static InputStream openURI(final URI uri) throws Exception {
        final int CONNECTION_TIMEOUT = 180000;
        long startTime = 0;
        long endTime = 0;
        try {
            if (uri == null) {
                throw new IllegalArgumentException("uri is null");
            }
            final URL url = uri.toURL();
            startTime = System.currentTimeMillis();
            final URLConnection connection = url.openConnection();
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            final InputStream is = url.openStream();
            final int PAGE_SIZE = 4096;
            final int NUM_PAGES = 20;
            final BufferedInputStream bis = new BufferedInputStream(is, PAGE_SIZE * NUM_PAGES);
            return bis;
        } catch (ConnectException e) {
            endTime = System.currentTimeMillis();
            System.err.println((endTime - startTime) + " millisecs before timeout.");
            throw e;
        } catch (Exception e) {
            System.err.println(e.toString());
            throw e;
        }
    }
}
