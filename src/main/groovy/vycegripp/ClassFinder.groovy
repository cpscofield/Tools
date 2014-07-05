// =============================================================================
// ClassFinder by Cary Scofield (carys689 <at> gmail <dot> com) is licensed under
// a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
// (See http://www.creativecommons.org for details).
//
// RECIPIENT ACCEPTS THE GOODS “AS IS,” WITH NO REPRESENTATION OR WARRANTY
// OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION IMPLIED
// WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
// =============================================================================

package vycegripp

import java.util.jar.*

class ClassFinder {

    private ClassPath classpath = null
    private boolean traverse_directories_recursively = true
    private String classname = null
    private String unpreprocessed_classname = null
    private boolean trace_search = false
    private long file_count = 0

    /**
     * <p>
     * Given a classname, this program will search specified directories, JAR files,
     * and/or the classpath for the location of the class.
     * </p>
     *
     * <p>
     * Usage:
     * </p>
     *
     * <p>
     * java ClassFinder [options]
     * </p>
     *
     * <p>
     * [options]: There are supposed to be 1 or more positional arguments. The first
     * positional argument is always considered to be the classname we are searching
     * for. The optional additional arguments are a list of jar files and/or
     * directories to be searched. If there is no '-classpath' option and no jar
     * files or directories specified, then the program will use the classpath from
     * the Java system property 'java.class.path'.
     * </p>
     *
     * @since 1.5
     * @author carys689 <at> gmail <dot> com
     */

    private static void dumpStringArray( String name, String[] strings ) {
        System.out.print( name + ": " )
        for( String string : strings ) {
            System.out.print( string + " " )
        }
        System.out.println("")
    }

    public static void main( String[] args ) {
        try {

            ClassFinder cf = new ClassFinder()

            //
            // Get the arguments
            //
            String[] newargs = cf.processArgs(args)
            if (newargs == null && cf.getClasspath() == null) {
                cf.setClasspath(System.getProperty("java.class.path"))
                System.out.println("Using java.class.path property: " + cf.getClasspath())
            }
            if (cf.getClassname() == null) {
                System.err.println("No classname on command line!")
                System.exit(1)
            } else {
                System.out.println("Searching for class: " + cf.getUnpreprocessedClassname() + " ...")
            }

            //
            // Do the search(es)
            //
            long start_time = System.currentTimeMillis()
            String[] jarList = cf.processClasspath(cf.getClassname(), cf.getClasspath())
            String[] jarOrDirList = cf.processDirsOrJars(cf.getClassname(), newargs, cf.isRecursive())
            long end_time = System.currentTimeMillis()
            System.out.println("Examined " + cf.getCount() + " files in " + ((float) (end_time - start_time)) / 1000.0 + " seconds")

            //
            // Print the results
            //
            boolean found_something = false
            if (jarList != null && jarList.length > 0) {
                found_something = true
                System.out.println("The class " + cf.getUnpreprocessedClassname() + " was found in the following files in the CLASSPATH")
                for( item in jarList ) {
                    System.out.println("  " + item)
                }
            }
            if (jarOrDirList != null && jarOrDirList.length > 0) {
                found_something = true
                System.out.println("The class " + cf.getUnpreprocessedClassname() + " was found in the following directory, class, or jar file(s)")
                for( item in jarOrDirList ) {
                    System.out.println("  " + item)
                }
            }
            if (!found_something) {
                System.out.println("Could not find " + cf.getUnpreprocessedClassname() + " in specified search space")
            }
        } catch (Exception e) {
            e.printStackTrace()
        }

    }

    /**
     * Get the classname string. This would be the "preprocessed" version of the
     * classname, in other words, all '.' characters have been converted to '/'
     * characters.
     */
    public String getClassname() {
        return this.classname
    }

    /**
     * Get the unpreprocessed classname string. The 'unpreprocessed' classname
     * is the name exactly as the user provided.
     */
    public String getUnpreprocessedClassname() {
        return this.unpreprocessed_classname
    }

    /**
     * Are we doing a recursive traversal of directories?
     */
    public boolean isRecursive() {
        return this.traverse_directories_recursively
    }

    /**
     * Get the trace mode value.
     */
    public boolean getTrace() {
        return this.trace_search
    }

    /**
     * The the count of the number of files examined in the search.
     */
    public long getCount() {
        return this.file_count
    }

    /**
     * Process command-line arguments and options. There are supposed to be 1 or
     * more positional arguments. The first positional argument is always
     * considered to be the classname we are searching for. The optional
     * additional arguments are a list of jar files and/or directories to be
     * searched. If there is no '-classpath' option and no jar files or
     * directories specified, then the program will use the classpath from the
     * Java system property 'java.class.path'.
     */
    private String[] processArgs(String[] args) {
        boolean classpath_arg_should_be_next = false
        ArrayList dir_or_jar = null // A list of directories or jar files to be searched
        boolean first_arg = true
        for( arg in args ) {
            if (classpath_arg_should_be_next) {
                this.classpath = new ClassPath(arg)
                classpath_arg_should_be_next = false
            } else if ("-classpath".equals(arg) || "-cp".equals(arg)) {
                classpath_arg_should_be_next = true
            } else if ("-norecurse".equals(arg)) {
                this.traverse_directories_recursively = false
            } else if ("-trace".equals(arg)) {
                this.trace_search = true
            } else {
                if (first_arg) {
                    this.unpreprocessed_classname = arg
                    this.classname = preprocessClassname(this.unpreprocessed_classname)
                    first_arg = false
                } else {
                    if (dir_or_jar == null) {
                        dir_or_jar = new ArrayList()
                    }
                    dir_or_jar.add(arg)
                }
            }
        }
        if (this.classpath == null) {
            this.classpath = new ClassPath()
        }
        return dir_or_jar.toArray()
    }

    /**
     * Get the classpath string.
     */
    public String getClasspath() throws Exception { //ClassPath.ClassPathException {
        //        return this.classpath.getClassPath()
        return this.classpath.classPath
    }

    /**
     * Set the classpath string.
     */
    public void setClasspath(String classpath) {
        this.classpath = new ClassPath(classpath)
    }

    private static String preprocessClassname(String class_name) {
        StringBuilder buffer = new StringBuilder(class_name)
        for (int i = 0; i < buffer.length(); ++i) {
            if (buffer.charAt(i) == '.') {
                buffer.replace(i, i + 1, "/")
            }
        }
        if (!class_name.endsWith(".class")) {
            return buffer.toString() + ".class"
        } else {
            return buffer.toString()
        }
    }

    /**
     * Search for the classname. Examine all the components of the classpath
     * string.
     */
    public String[] processClasspath(String classname, String classpath) throws Exception { //ClassPath.ClassPathException {
        ArrayList list = new ArrayList()
        try {
            if (classpath != null) {
                String[] tempClassPath = this.classpath.toArray()
                for( component in tempClassPath ) {
                    if( component.endsWith("*") ) {
                        processDirectory(classname, dir, "*.jar", list, false )
                    }
                    else {
                        if (component.endsWith(".jar") || component.endsWith(".war") || component.endsWith(".ear")) {
                            processJarFile(classname, new File(component), list)
                        } else {
                            File dir = new File(component)
                            if (dir.isDirectory()) {
                                processDirectory(classname, dir, "*", list, false)
                            }
                        }
                    }
                }
            }
        }
        catch( Exception e ) {
            e.printStackTrace()
            throw e
        }
        return list.toArray()
    }

    /**
     * Search for the classname. Search the list of directories and jar files.
     */
    public String[] processDirsOrJars(String classname, String[] dirsOrJars, boolean recursive) {
        ArrayList list = new ArrayList()
        if (dirsOrJars == null) {
            return null
        }
        for (dirOrJar in dirsOrJars) {
            if (dirOrJar.endsWith(".jar") || dirOrJar.endsWith(".war") || dirOrJar.endsWith(".ear")) {
                processJarFile(classname, new File(dirOrJar), list)
            } else {
                File trythis = new File(dirOrJar)
                if (trythis.isDirectory()) {
                    processDirectory(classname, trythis, "*", list, recursive)
                }
                else {
                    System.out.println( "Not a directory?: " + dirOrJar )
                }
            }
        }
        return list.toArray()
    }

    /**
     * Search for the classname in a single JAR file.
     */
    public void processJarFile(String classname, File file, ArrayList jarFiles) {
        JarFile jarFile = null
        if (this.trace_search) {
            System.out.println("Processing JAR file: " + file.getPath())
        }
        try {
            if (!file.exists()) {
                System.err.println("Warning: JAR file does not exist: " + file)
                return
            }
            jarFile = new JarFile(file)
            Enumeration entries = jarFile.entries()
            while (entries.hasMoreElements()) {
                JarEntry entry = (JarEntry) entries.nextElement()
                String entry_name = entry.getName()
                if (entry_name.endsWith(".class")) {
                    if (entry_name.indexOf(classname) != -1) {
                        if (this.trace_search) {
                            System.out.println("Found possible match: " + entry_name + " in " + file.getPath())
                        }
                        appendToList(jarFiles, jarFile.getName())
                    }
                }
            }
            this.file_count++
        } catch (IOException e) {
            System.err.println(e + ": " + file.getName())
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close()
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Append a String object to an ArrayList.
     */
    private static void appendToList(ArrayList list, String item) {
        if (list == null) {
            list = new ArrayList();
        }
        list.add(item);
    }

/**
     * Search for a classname in a directory. Optionally recursively traverse
     * any subdirectories found.
     *
     * @param classname The classname we are searching for. Must be a
     * "preprocessed" classname.
     * @param directory The directory in which to conduct the search.
     * @param wildcard
     * @param dirsOrJars The accumulating list of JAR file(s) and/or
     * directories into which we will place the names of JAR files and/or
     * directories where the class was found.
     * @param recursive Indicates whether or not to pursue recursive traversal
     * of any subdirectories found.
     */
    public void processDirectory(String classname, File directory, String wildcard, ArrayList dirsOrJars, boolean recursive) {
        if (this.trace_search) {
            System.out.println("Processing directory: " + directory.getPath())
        }

        String[] files = null
        if( wildcard == "*" ) {
            files = directory.list()
        }
        else {
            if( !wildcard.equals( "*.jar") ) {
                throw new RuntimeException( "Invalid wildcard expression: " + wildcard )
            }
            FilenameFilter filter = wildcard
            files = directory.list(filter)
        }
        if (files == null) {
            return
        }
        for (elem in files) {
            File file = new File(directory.getPath() + System.getProperty( "file.separator") + elem)

            if (file.isDirectory() && recursive) {
                processDirectory(classname, file, "*", dirsOrJars, recursive)
            } else if (file.getName().endsWith(".jar") || file.getName().endsWith(".war") || file.getName().endsWith(".ear")) {
                processJarFile(classname, file, dirsOrJars)
            } else if (elem.endsWith(".class")) {
                if (elem.length() != classname.length()) {
                    continue
                }
                try {
                    if (elem.substring(0, classname.length()).equals(classname)) {
                        appendToList(dirsOrJars, file.getPath())
                    }
                } catch (Exception e) {
                    System.err.println(e)
                    System.err.println(elem + ":" + classname)
                    System.err.flush()
                }
            } else {
                continue
            }
        }
    }
}

