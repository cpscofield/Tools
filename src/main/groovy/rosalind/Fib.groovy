//============================================================================
//
//Solution to Rosalind challenge "FIB: Rabbits and Recurrence Relations"
//See http://rosalind.info/problems/fib for details.
//
//Given:
//
//    Positive integers nâ‰¤40 and kâ‰¤5.
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
package rosalind

class Tuple2 {
    int n
    int k
    Tuple2( int n, int k ) {
        this.n = n
        this.k = k
    }
}

def readinput( path ) {
    Scanner scanner = new Scanner( new File( path ) )
    n = scanner.nextInt()
    k = scanner.nextInt()
    new Tuple2( n, k )
}

def fib( long n, long k ) {
    if( n < 3 ) 1
    else fib( n - 1, k ) + k * fib( n - 2, k )
}

def execute() {
    Tuple2 params = readinput( "c:/downloads/rosalind_fib.txt" )
    println( "n=" + params.n + " k=" + params.k )
    println( fib( params.n, params.k ) )
}

start = System.currentTimeMillis();
execute()
end = System.currentTimeMillis();
System.out.format( "Total time: %5.3f second(s)", ((end-start)/1000.0D))