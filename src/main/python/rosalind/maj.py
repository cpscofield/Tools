"""
Solution to Rosalind challenge "MAJ: Majority Element"
See http://rosalind.info/problems/maj for details.

Given:

    A positive integer k≤20, a positive integer n≤10^4,
    and k arrays of size n containing positive integers
    not exceeding 10^5.

Return:

    For each array, output an element of this array occurring
    strictly more than n/2 times if such element exists, and "-1" otherwise.
    
Author:

    Cary Scofield
    carys689 <at> gmail <dot> com

"""

from time import clock
from collections import defaultdict

def readinput( path ):
    f = open( path, "r" )
    k, n = [int(c) for c in f.readline().strip().split()]
    print( "k=%d n=%d" % (k,n),flush=True)
    arrays = []
    for i in range(k):
        array = [int(c) for c in f.readline().strip().split()]
        print( "line " + str(i), str(array[0]), str(array[-1:]),flush=True)
        arrays.append(array)
    f.close()
    return k, n, arrays

def evaluate(a):
    tallies = defaultdict()
    for i in range(len(a)):
        if a[i] in tallies:
            tallies[a[i]] += 1
        else:
            tallies[a[i]] = 1
    half = len(a) // 2 if len(a) & 0x1 == 0 else (len(a) // 2) + 1
    majority = -1
    maxcount = 0
    for i in tallies.items():
        tally = i[1]
        if tally <= half:
            continue
        if tally > maxcount:
            majority = i[0]
            maxcount = tally
    return majority

def execute():
    k, n, a = readinput( "c:/downloads/rosalind_maj (1).txt" )
    print()
    results = []
    output = open( "c:/temp/output_maj.txt", "w" )
    for i in range(len(a)):
        print( "Evaluating a[%d]" % i,flush=True)
        result = evaluate(a[i])
        output.write(str(result))
        output.write(" ")
        #print( result,end=" ")
    #print("\n")
    output.write("\n")
    output.close()

if __name__ == "__main__":
    start_time = clock()
    execute()
    print( "Total time=%.3f second(s)" % (clock()-start_time) )
