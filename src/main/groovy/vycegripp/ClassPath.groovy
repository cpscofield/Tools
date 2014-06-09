package vycegripp

import java.util.jar.*

public final class ClassPath {
    private final String classPath
    public ClassPath( final String classPath ) {
        if( classPath == null ) this.classPath = System.getProperty("java.class.path")
        else this.classPath = classPath
    }
    public String[] toArray() {
        ArrayList<String> list = new ArrayList()
        StringTokenizer tokenizer = new StringTokenizer(classPath, File.pathSeparator)
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken()
            list.add(token);
        }
        list.toArray()
    }
    public static class ClassPathException extends Exception {
        public ClassPathException() { super() }
        public ClassPathException(String msg) { super(msg) }
        public ClassPathException(String msg, Throwable cause ) {  super( msg, cause ) }
        public ClassPathException(Throwable cause ) { super(cause) }
    }
}

