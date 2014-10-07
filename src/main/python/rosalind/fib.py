"""
Solution to Rosalind challenge "FIB: Rabbits and Recurrence Relations"
See http://rosalind.info/problems/fib for details.

Given:

    Positive integers n≤40 and k≤5.

Return:

    The total number of rabbit pairs that will be present after
    n months if we begin with 1 pair and in each generation,
    every pair of reproduction-age rabbits produces a litter of
    k rabbit pairs (instead of only 1 pair).
    
Author:

    Cary Scofield
    carys689 <at> gmail <dot> com

"""

def readinput( path ):
    f = open( path, "r" )
    n, k = [int(c) for c in f.readline().split() ]
    f.close()
    return n, k

def fib( n, k ):
    if n < 3:
        return 1
    else:
        return fib( n - 1, k ) + k * fib( n - 2, k )

def execute():
    ( n, k ) = readinput( "c:/downloads/rosalind_fib.txt" )
    print( "n=%d k=%d" % (n,k))
    print( fib( n, k ) )
    
           
if __name__ == "__main__":
    execute()
