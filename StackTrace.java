// =============================================================================
// StackTrace by Cary Scofield (carys689@gmail.com) is licensed under a 
// Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
// (See http://www.creativecommons.org for details).
//
// RECIPIENT ACCEPTS THE GOODS “AS IS,” WITH NO REPRESENTATION OR WARRANTY 
// OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION IMPLIED 
// WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
// =============================================================================

package vycegripp;

/**
 * A very simple, but very convenient tool to produce an arbitrary, printable
 * stack trace from wherever it is invoked.
 * <p>
 * Usage:
 * <code>
 *      System.out.println( new StackTrace().toString() );
 * </code>
 * @author SCOFIELD
 */
public final class StackTrace {

    private final java.lang.StackTraceElement[] elements;
    private final String header;

    public StackTrace() {
        this( "vycegripp.StackTrace" );
    }
    
    public StackTrace( String header ) {
        this.header = header;
        try {
            throw new Throwable();
        } catch (Throwable t) {
            this.elements = t.getStackTrace();
        }
        
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append( this.header ).append( "\n" );
        for( int i = 2; i < this.elements.length; ++i ) { // skip the 1st two elements
            buf.append("\tat ").append(elements[i].toString()).append("\n");
        }
        return buf.toString();
    }
}
