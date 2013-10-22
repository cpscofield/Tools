// =============================================================================
// Stringify by Cary Scofield (carys689 <at> gmail <dot> com) is licensed under a 
// Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
// (See http://www.creativecommons.org for details).
//
// RECIPIENT ACCEPTS THE GOODS “AS IS,” WITH NO REPRESENTATION OR WARRANTY 
// OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION IMPLIED 
// WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
// =============================================================================

package vycegripp;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.lang.reflect.*;

/**
 * <p>
 * This class is a utility that uses Java reflection to print out the string contents, 
 * to the best extent possible, of the individual fields of an object. 
 * An example of usage is to print such data to a log file. In
 * addition, there is the capability to either exclude the contents of certain fields from
 * being emitted or only include the contents of certain fields to be emitted.
 * </p>
 * <p>
 * Usage:
 * </P>
 * <p>Suppose you had a class defined thusly:
 * </p>
 * <code>
 *     public class TestClass {
 *       private int i = 5;
 *       private double x = 3.4D;
 *       private float y = (float) 1.2;
 *       private boolean t = true;
 *       private boolean f = false;
 *       private char c = 'A';
 *       private String s = "LetErRip";
 *       private long l = -1L;
 *       private short j = 16363;
 *       private int[] a = { 1, 2, 3, 4 };
 *       private String card = "429036049519943";
 *       }
 * </code>
 * <p>
 * The contents of the object can be displayed thusly:
 * </p>
 * <code>
 *      TestClass tc = new TestClass();
 *      System.out.println( new Stringify( tc ).toString() );
 * </code>
 * <p>
 * The output will look like the following:
 * </p>
 * <code>
 * {i=5;x=3.4;y=1.2;t=true;f=false;c=A;s="LetErRip";l=-1;j=16363;a:{[0]=1,[1]=2,[2]=3,[3]=4};card="429036049519943"}
 * </code>
 * <p>
 * If, for example, you wanted to exclude the field 'card' from the output:
 * </p>
 * <code>
 *     System.out.println( new Stringify( tc ).exclude("card").toString() );
 * </code>
 * <p>
 * Or, if you wanted to display only the fields 's', 'j', and 'a':
 * </p>
 * <code>
 *     System.out.println( new Stringify( tc ).include( "s", "j", "a" ).toString() );
 * </code>
 * <p>
 * Excluded fields and included fields cannot be combined.
 * <p>
 * Lists, Sets, and Maps are printed to the best extent possible.
 * </p>
 * 
 * @since 1.7
 * @author Cary Scofield (carys689 <at> gmail <dot> com)
 */

public class Stringify {

    private Set<String> excludedNames = new HashSet<String>();
    private Set<String> includedNames = new HashSet<String>();
    private Object object;
    
    /**
     * Constructor
     * @param object The object to be 'stringified'.
     */
    public Stringify(Object object) {
        this.object = object;
    }

    /**
     * Names of fields to be excluded from the output. This is mutually-exclusive
     * of any overtly included names.
     * @param names Variable argument list of names of fields to be excluded.
     * @return Stringify object.
     */
    public Stringify exclude(String... names) {
        for (String name : names) {
            this.excludedNames.add(name.trim());
        }
        return this;
    }

    /**
     * Names of fields to be included in the output. This is mutually-exclusive
     * of any overtly excluded names.
     * @param names Variable argument list of names of fields to be included.
     * @return Stringify object.
     */
    public Stringify include(String... names) {
        for (String name : names) {
            this.includedNames.add(name.trim());
        }
        return this;
    }

    /**
     * Emit the 'stringified' contents of the object supplied in the constructor.
     * @return Contents of object as a <tt>String</tt>.
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{");
        try {
            Class klass = object.getClass();
            List<Field> fields = new LinkedList<Field>();
            while (klass != null) {
                Field[] f = klass.getDeclaredFields();
                for (int i = 0; i < f.length; ++i) {
                    fields.add(f[i]);
                }
                klass = klass.getSuperclass();
            }
            for (Field field : fields) {
                field.setAccessible(true);
                Class type = field.getType();
                String name = field.getName();
                if (this.excludedNames.contains(name)) {
                    continue;
                }
                if (this.excludedNames.isEmpty()
                        && !this.includedNames.isEmpty()
                        && !this.includedNames.contains(name)) {
                    continue;
                }
                if (type.isArray()) {
                    int len = Array.getLength(field.get(this.object));
                    buf.append(name + ":");
                    buf.append("{");
                    for (int i = 0; i < len; ++i) {
                        if (i > 0) {
                            buf.append(",");
                        }
                        if (type.toString().startsWith("class [Ljava.lang.String;")) {
                            buf.append("[" + i + "]=\"").
                                    append(Array.get(field.get(this.object), i)).append("\"");
                        } else {
                            buf.append("[" + i + "]=").
                                    append(Array.get(field.get(this.object), i));
                        }
                    }
                    buf.append("};");
                } else {
                    Object value = field.get(this.object);
                    if (type.toString().endsWith(".String")) {
                        buf.append(name + "=\"" + value.toString() + "\";");
                    } else {
                        buf.append(name + "=" + value.toString() + ";");
                    }
                }
            }

        } catch (Exception e) {
            return e.toString();
        }
        buf.append("}");
        return buf.toString();
    }
    
}
