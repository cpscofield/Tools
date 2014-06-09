package vycegripp

import java.util.jar.*

class ClassFinder {

    private ClassPath classpath = null
    private boolean traverse_directories_recursively = true
    private String classname = null
    private String unpreprocessed_classname = null
    private boolean trace_search = false
    private final static boolean DEBUGGING = false
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
     * directories to be searched. If there is no '-classpath' option or no jar
     * files or directories specified, then the program will use the classpath from
     * the Java system property 'java.class.path'.
     * </p>
     *
     * <p>
     *     Example:
     *     <pre>
     *       java vycegripp.ClassFinder NameValuePair c:\users\scofield\documents\stash\sandbox\javaex\netbeansprojects\stockgrader
     *       Searching for class: NameValuePair ...
     *       Examined 65 files in 0.354 seconds
     *       The class NameValuePair was found in the following files in the CLASSPATH
     *           C:\Program Files (x86)\Java\jdk1.7.0_25\jre\lib\rt.jar
     *       The class NameValuePair was found in the following directory, class, or jar file(s)
     *           c:\users\scofield\documents\stash\sandbox\javaex\netbeansprojects\stockgrader\build\classes\vycegripp\stockgrader\NameValuePair.class
     *           c:\users\scofield\documents\stash\sandbox\javaex\netbeansprojects\stockgrader\dist\StockGrader.jar
     *       </pre>
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
            String[] jar_list = cf.processClasspath(cf.getClassname(), cf.getClasspath())
            String[] jar_or_dir_list = cf.processDirsOrJars(cf.getClassname(), newargs, cf.isRecursive())
            long end_time = System.currentTimeMillis()
            System.out.println("Examined " + cf.getCount() + " files in " + ((float) (end_time - start_time)) / 1000.0 + " seconds")

            //
            // Print the results
            //
            boolean found_something = false
            if (jar_list != null && jar_list.length > 0) {
                found_something = true
                System.out.println("The class " + cf.getUnpreprocessedClassname() + " was found in the following files in the CLASSPATH")
                for (int i = 0; i < jar_list.length; ++i) {
                    System.out.println("  " + jar_list[i])
                }
            }
            if (jar_or_dir_list != null && jar_or_dir_list.length > 0) {
                found_something = true
                System.out.println("The class " + cf.getUnpreprocessedClassname() + " was found in the following directory, class, or jar file(s)")
                for (int i = 0; i < jar_or_dir_list.length; ++i) {
                    System.out.println("  " + jar_or_dir_list[i])
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
     * For debugging.
     */
    private static void dumpStrings(String[] strings) {
        if (strings != null) {
            for (int i = 0; i < strings.length; ++i) {
                System.out.println("strings[" + i + "]=" + strings[i])
            }
        } else {
            System.out.println("strings is null")
        }
    }

    /**
     * For debugging.
     */
    private static void dumpStrings(ArrayList strings) {
        dumpStrings(convertArrayListToStringArray(strings))
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
        for( int i = 0; i < args.length; ++i ) {
            if (classpath_arg_should_be_next) {
                this.classpath = new ClassPath(args[i])
                classpath_arg_should_be_next = false
            } else if ("-classpath".equals(args[i]) || "-cp".equals(args[i])) {
                classpath_arg_should_be_next = true
            } else if ("-norecurse".equals(args[i])) {
                this.traverse_directories_recursively = false
            } else if ("-trace".equals(args[i])) {
                this.trace_search = true
            } else {
                if (first_arg) {
                    this.unpreprocessed_classname = args[i]
                    this.classname = preprocessClassname(this.unpreprocessed_classname)
                    first_arg = false
                } else {
                    if (dir_or_jar == null) {
                        dir_or_jar = new ArrayList()
                    }
                    dir_or_jar.add(args[i])
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
                for (int i = 0; i < tempClassPath.length; ++i) {
                    String component = tempClassPath[i]
                    if (component.endsWith(".jar") || component.endsWith(".war") || component.endsWith(".ear")) {
                        processJarFile(classname, new File(component), list)
                    } else {
                        File file = new File(component)
                        if (file.isDirectory()) {
                            processDirectory(classname, file, list, false)
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
    public String[] processDirsOrJars(String classname, String[] dirs_or_jars, boolean recursive) {
        ArrayList list = new ArrayList()
        if (dirs_or_jars == null) {
            return null
        }
        for (int i = 0; i < dirs_or_jars.length; ++i) {
            if( DEBUGGING ) System.out.println( "dir_or_jar: " + dirs_or_jars[i] )
            if (dirs_or_jars[i].endsWith(".jar") || dirs_or_jars[i].endsWith(".war") || dirs_or_jars[i].endsWith(".ear")) {
                processJarFile(classname, new File(dirs_or_jars[i]), list)
            } else {
                File trythis = new File(dirs_or_jars[i])
                if (trythis.isDirectory()) {
                    processDirectory(classname, trythis, list, recursive)
                }
                else {
                    System.out.println( "Not a directory?: " + dirs_or_jars[i] )
                }
            }
        }
        return list.toArray()
    }

    /**
     * Search for the classname in a single JAR file.
     */
    public void processJarFile(String classname, File file, ArrayList jar_files) {
        JarFile jar_file = null
        if (this.trace_search) {
            System.out.println("Processing JAR file: " + file.getPath())
        }
        try {
            if (!file.exists()) {
                System.err.println("Warning: JAR file does not exist: " + file)
                return
            }
            jar_file = new JarFile(file)
            Enumeration entries = jar_file.entries()
            while (entries.hasMoreElements()) {
                JarEntry entry = (JarEntry) entries.nextElement()
                String entry_name = entry.getName()
                if (entry_name.endsWith(".class")) {
                    if (entry_name.indexOf(classname) != -1) {
                        if (this.trace_search) {
                            System.out.println("Found possible match: " + entry_name + " in " + file.getPath())
                        }
                        appendToList(jar_files, jar_file.getName())
                    }
                }
            }
            this.file_count++
        } catch (IOException e) {
            System.err.println(e + ": " + file.getName())
        } finally {
            if (jar_file != null) {
                try {
                    jar_file.close()
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
     * @param dirs_or_jars The accumulating list of JAR file(s) and/or
     * directories into which we will place the names of JAR files and/or
     * directories where the class was found.
     * @param recursive Indicates whether or not to pursue recursive traversal
     * of any subdirectories found.
     */
    public void processDirectory(String classname, File directory, ArrayList dirs_or_jars, boolean recursive) {
        if (this.trace_search) {
            System.out.println("Processing directory: " + directory.getPath())
        }
        String[] files = directory.list()
        if (files == null) {
            return
        }
        for (int i = 0; i < files.length; ++i) {
            if( DEBUGGING ) System.out.println( "Processing file system element: " + files[i] )
            File file = new File(directory.getPath() + System.getProperty( "file.separator") + files[i])
            //File file = new File(directory.getPath())
            if( DEBUGGING ) {
                if(file.isDirectory() )System.out.println( " ... is a directory" )
                else System.out.println( " ... is NOT a directory?" )
            }

            if (file.isDirectory() && recursive) {
                processDirectory(classname, file, dirs_or_jars, recursive)
            } else if (file.getName().endsWith(".jar") || file.getName().endsWith(".war") || file.getName().endsWith(".ear")) {
                if( DEBUGGING ) System.out.println( "About to process JAR file: " + file.getPath() )
                processJarFile(classname, file, dirs_or_jars)
            } else if (files[i].endsWith(".class")) {
                if (files[i].length() != classname.length()) {
                    continue
                }
                try {
                    if (files[i].substring(0, classname.length()).equals(classname)) {
                        appendToList(dirs_or_jars, file.getPath())
                    }
                } catch (Exception e) {
                    System.err.println(e)
                    System.err.println(files[i] + ":" + classname)
                    System.err.flush()
                }
            } else {
                continue
            }
        }
    }
}

