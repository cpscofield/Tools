// =============================================================================
// Gzipper by Cary Scofield (carys689@gmail.com) is licensed under a 
// Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
// (See http://www.creativecommons.org for details).
//
// RECIPIENT ACCEPTS THE GOODS “AS IS,” WITH NO REPRESENTATION OR WARRANTY 
// OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION IMPLIED 
// WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
// =============================================================================

package vycegripp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author Cary Scofield (carys689@gmail.com)
 * @since 1.7
 */
public class Gzipper {

    /**
     * Compress an array of bytes. 
     * Make sure input bytes are encoded with ISO-8859-1 character encoding.
     */
    public static byte[] compress( byte[] bytes ) throws Exception {
	ByteArrayInputStream bais = null;
	ByteArrayOutputStream baos = null;
	try {
	    bais = new ByteArrayInputStream( bytes );
	    baos = new ByteArrayOutputStream();
	    GZIPOutputStream gzos = new GZIPOutputStream( baos );
	    final int BUFSIZ = 4096;
	    byte inbuf[] = new byte[BUFSIZ];
	    int n;
	    while( ( n = bais.read( inbuf ) ) != -1 ) {
		gzos.write( inbuf, 0, n );
	    }
	    bais.close();
	    bais = null;
	    gzos.close();
	}
	catch( Exception e ) {
	    throw e;
	}
	finally {
	    try {
		if( bais != null ) bais.close();
		if( baos != null ) {
		    baos.close();
		    return baos.toByteArray();
		}
	    }
	    catch( Exception e ) {
		throw e;
	    }
	}
	return null;
    }

    /**
     * Decompress an array of bytes. 
     * Convert output bytes to String using ISO-8859-1 character encoding.
     */
    public static byte[] decompress( byte[] bytes ) throws Exception {
	if( bytes == null || bytes.length == 0 ) return bytes;
	ByteArrayInputStream bais = null;
	ByteArrayOutputStream baos = null;
	try {
	    bais = new ByteArrayInputStream( bytes );
	    baos = new ByteArrayOutputStream();
	    GZIPInputStream gzis = new GZIPInputStream( bais );
	    final int BUFSIZ = 4096;
	    byte[] inbuf = new byte[ BUFSIZ ];
	    int n;
	    while( ( n = gzis.read( inbuf, 0, BUFSIZ ) ) != -1 ) {
		baos.write( inbuf, 0, n );
	    }
	    gzis.close();
	    bais = null;
	    baos.close();
	}
	catch( Exception e ) {
	    throw e;
	}
	finally {
	    try {
		if( bais != null ) bais.close();
		if( baos != null ) {
		    baos.close();
		    return baos.toByteArray();
		}
	    }
	    catch( Exception e ) {
		throw e;
	    }
	}
	return null;
    }

    
}
