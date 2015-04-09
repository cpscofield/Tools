// =============================================================================
// Base64 by Cary Scofield (carys689 <at> gmail <dot> com) is licensed under 
// a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
// (See http://www.creativecommons.org for details).
//
// RECIPIENT ACCEPTS THE GOODS “AS IS,” WITH NO REPRESENTATION OR WARRANTY 
// OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION IMPLIED 
// WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
// =============================================================================


package vycegripp;

/**
 * This program is a utility making use of Java NIO library that either 
 * encodes a file into Base64 or decodes a file from Base64.
 * <p>
 * Usage:
 * <p>
 * To encode a file:
 * <code>
 * java -jar Base64.jar -e foo.xyz
 * </code>
 * <p>
 * Output will be placed in foo.xyz.base64
 * <p>
 * To decode a file:
 * <code>
 * java -jar Base64.jar -d foo.xyz.base64
 * </code>
 * Output will be place in foo.xyz
 * 
 * <p>
 * Requires Apache Commons Codec library, version 1.3 or later.
 * </p>
 * @author Cary Scofield (carys689 <at> gmail <dot> com)
 * @since 1.7
 */


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;

public class Base64 {
    
    private static final String BASE64_SUFFIX = ".base64";
    
    public static void main(String[] args) {
        try {
            if(args.length < 2 ) {
                System.err.println("Usage: [-e|-d] srcfile dstfile");
                System.exit(1);
            }
            if("-e".equals(args[0])) {
                byte[] encoded = encode(args[1]);
                createFile(args[1] + BASE64_SUFFIX, encoded);
            }
            else if("-d".equals(args[0])) {
                byte[] decoded = decode(args[1]);
                String newPath = args[1].substring(0,args[1].indexOf(BASE64_SUFFIX));
                createFile(newPath, decoded);
            }
            else {
                throw new Exception("1st arg must be '-e' or '-d'");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }
    
    private static byte[] encode(String path) throws Exception {
        return org.apache.commons.codec.binary.Base64.encodeBase64(readFile(path));
    }
    
    private static byte[] decode(String path) throws Exception {
        return org.apache.commons.codec.binary.Base64.decodeBase64(readFile(path));
    }
    
    private static void createFile(String path, byte[] bytes) throws Exception {
        File file = new File(path);
        try(FileOutputStream fos = new FileOutputStream(file)) {
            FileChannel fc = fos.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            for(int i = 0; i < bytes.length; ++i) {
                buffer.put(bytes[i]);
            }
            buffer.flip();
            fc.write(buffer);
        }
    }
    
    private static byte[] readFile(String filepath) throws Exception {
        File file = new File(filepath);
        if(file.length() > 0x7FFFFFFFL) {
            throw new Exception("File is too large. Maximum size: 2GB");
        }
        try(FileInputStream fis = new FileInputStream(file)){
            FileChannel fc = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate((int)(0x7FFFFFFF&file.length()));
            fc.read(buffer);
            return buffer.array();
        }
    }
}
