/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package rosalind;

import java.io.*;
import java.util.*;

/**
 *
 * @author SCOFIELD
 */
public class Fib {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Long start = System.currentTimeMillis();
            execute();
            Long end = System.currentTimeMillis();
            System.out.format( "Total time = %5.3f%n", (( end - start ) / 1000.0D));
        }
        catch( Exception e ) {
            e.printStackTrace();
            System.exit( 1 );
        }
        System.exit(0);
    }
    
    private static class Tuple2 {
        public int n;
        public int k;
    }
    
    private static long fib( long n, long k ) {
        if( n < 3 ) return 1L;
        else
            return fib( n - 1, k ) + k * fib( n - 2, k );
    }
    
    private static void execute() throws Exception {
        Tuple2 params = readinput( "c:/downloads/rosalind_fib.txt" );
        System.out.println( "n=" + params.n + " k=" + params.k );
        System.out.println( fib( params.n, params.k ) );
    }
    
    private static Tuple2 readinput( String path ) throws Exception {
        Tuple2 params = new Tuple2();
        File file = new File( path );
        Scanner scanner = new Scanner( file );
        params.n = scanner.nextInt();
        params.k = scanner.nextInt();
        scanner.close();
        return params;
    }
    
}
