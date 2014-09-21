# -*- coding: utf-8 -*-
"""
Solution to Rosalind challenge "1D: Clump Finding Problem"
See http://rosalind.info/problems/1d for details.

Given:

    A string Genome, and integers k, L, and t.

Return:

    All distinct k-mers forming (L, t)-clumps in Genome.
    
Author:

    Cary Scofield
    carys689 <at> gmail <dot> com

"""

from utils import Utils
utils = Utils()

def readinput( path ):
    """
    Read input 
    """
    fhandle = open( path, "r" )
    genome = fhandle.readline().rstrip()
    params = [int(c) for c in fhandle.readline().rstrip().split()]
    fhandle.close()
    k = params[0]
    L = params[1]
    t = params[2]
    print( "%s\nlen=%d" % (genome, len(genome) ) )
    print( "k=%d L=%d t=%d" % (k, L, t ) )
    return genome,k,L,t

def get_kmers(sequence, k ):
    """
    Get all k-mers in sequence.
    """
    kmers = []
    for i in range(len(sequence)):
        s = sequence[i:i+k]
        if len(s) < k: break
        kmers.append(s)
    return kmers
    
def find_clumps( genome, k, L, t ):
    clumps=[]

    """
    Get all the k-mers from the genome.
    """

    kmers = get_kmers( genome[0:L], k )
    for i in range(1,len(genome)):
        if L+i >= len(genome): break
        kmers.extend( get_kmers( genome[L-k+i:L+i], k ) )

    print( "Tallying the k-mers..." )
    """
    Tally all the k-mers found.
    """
    kmer_dict = dict();
    for i in range(len(kmers)):
        if kmers[i] in kmer_dict:
            count = kmer_dict[kmers[i]]
            count += 1
            if( count > t ):
                pass #del kmer_dict[kmers[i]] # count exceeding t value
            else:
                kmer_dict[kmers[i]] = count
        else:
            kmer_dict[kmers[i]] = 1

    limit = 10
    print( "Top %d k-mers..." % limit)
    count = 0
    for i in sorted(kmer_dict, key=kmer_dict.get, reverse=True ):
        count += 1
        if count > limit: break
        print( i, kmer_dict[i] )

    """
    Find those k-mers whose count is equal to t.
    """
    print( "# items in kmer_dict=%d" % len(kmer_dict) )
    for (kmer,count) in kmer_dict.items():
        if count == t:
            print( kmer, count )
            clumps.append( kmer )

    print( "Found %d clump(s) whose k-mer count is equal to %d" % (len(clumps),t) )
    
    return clumps


def output( ohandle, result, sep='\n' ):
    print( str(result) )
    ohandle.write( str(result) + sep )

def execute():
    (genome,k,L,t) = readinput("rosalind_1d.txt")
    clumps = find_clumps(genome,k,L,t)
    ohandle = open( "output_1d.txt", "w" )
    output( ohandle, utils.list2string(clumps) )
    
            
if __name__ == '__main__':
    execute()
