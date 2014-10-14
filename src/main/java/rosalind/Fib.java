//============================================================================
//
//Solution to Rosalind challenge "FIB: Rabbits and Recurrence Relations"
//See http://rosalind.info/problems/fib for details.
//
//Given:
//
//    Positive integers n≤40 and k≤5.
//
//Return:
//
//    The total number of rabbit pairs that will be present after
//    n months if we begin with 1 pair and in each generation,
//    every pair of reproduction-age rabbits produces a litter of
//    k rabbit pairs (instead of only 1 pair).
//
//Author:
//
//    Cary Scofield
//    carys689 <at> gmail <dot> com
//
//============================================================================
package rosalind;

import java.io.*;
import java.util.*;

public class Fib {

    public static void main(String[] args) {
        try {
            Long start = System.currentTimeMillis();
            execute();
            Long end = System.currentTimeMillis();
            System.out.format("Total time = %5.3f%n", ((end - start) / 1000.0D));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

    private static long fib(long n, long k) {
        if (n < 3) {
            return 1L;
        } else {
            return fib(n - 1, k) + k * fib(n - 2, k);
        }
    }

    private static class Tuple2<T extends Number,U extends Number> {
        public T n;
        public U k;
    }

    private static void execute() throws Exception {
        Tuple2<Long,Long> params = readinput("c:/downloads/rosalind_fib.txt");
        System.out.println("n=" + params.n + " k=" + params.k);
        System.out.println(fib(params.n, params.k));
    }

    private static Tuple2<Long,Long> readinput(String path) throws Exception {
        Tuple2<Long,Long> params = new Tuple2<>();
        File file = new File(path);
        try (Scanner scanner = new Scanner(file)) {
            params.n = scanner.nextLong();
            params.k = scanner.nextLong();
        }
        return params;
    }

}
