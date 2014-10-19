"""
Solution to Rosalind challenge "CONS: Consensus and Profile"
See http://rosalind.info/problems/cons for details.

Given:

    A collection of at most 10 DNA strings of equal length
    (at most 1 kbp) in FASTA format.

Return:

    A consensus string and profile matrix for the collection.
    (If several possible consensus strings exist, then you may
    return any one of them.)
    
Author:

    Cary Scofield
    carys689 <at> gmail <dot> com

"""

from time import clock
from fasta import Fasta

def readinput( path ):
    """
    Read all the segments from FASTA file. Return Fasta object.
    """
    fasta = Fasta( path )
    for i in range( len(fasta.get_segments()) ):
        seg = fasta.get_segments()[i]
        seq = seg.get_sequence()
        print( seg.get_header() + "\n" + seq )
    return fasta

def calculate_consensus( fasta ):
    """
    For each of the sequences in each of the FASTA segments, count the
    number of A,C,G,T nucleotides in each column. The length of sequences
    are required to be of equal length. Once the tallies have been
    completed, create a consensus genome of the highest tally of each
    column from the input genomes.
    """
    n_columns = len(fasta.get_segments()[0].get_sequence())
    zeros = [0] * n_columns
    a_counts = zeros[:]
    c_counts = zeros[:]
    g_counts = zeros[:]
    t_counts = zeros[:]
    for i in range( len( fasta.get_segments() ) ):
        seg = fasta.get_segments()[i]
        seq = seg.get_sequence().upper()
        if len(seq) != n_columns:
            raise "Genome has wrong length. Expected: " + \
                  n_columns + ", got " + len(seq)
        for j in range( len( seq ) ):
            if seq[j] == 'A':
                a_counts[j] += 1
            elif seq[j] == 'C':
                c_counts[j] += 1
            elif seq[j] == 'G':
                g_counts[j] += 1
            elif seq[j] == 'T':
                t_counts[j] += 1
            else:
                raise "Unrecognized nucleotide: " + seq[j]

##    print( "A: " + ' '.join( map( str, a_counts ) ) )
##    print( "C: " + ' '.join( map( str, c_counts ) ) )
##    print( "G: " + ' '.join( map( str, g_counts ) ) )
##    print( "T: " + ' '.join( map( str, t_counts ) ) )
    
    
    consensus_sequence = []        
    for i in range( len(a_counts) ):
        consensus_nucleotide = 'A'
        counts = a_counts[i]
        if c_counts[i] > counts:
            consensus_nucleotide = 'C'
            counts = c_counts[i]
        if g_counts[i] > counts:
            consensus_nucleotide = 'G'
            counts = g_counts[i]
        if t_counts[i] > counts:
            consensus_nucleotide = 'T'
            counts = t_counts[i]
##        print( "i=" + str(i) + " counts=" + str(counts) + " " + consensus_nucleotide )
        consensus_sequence.append( consensus_nucleotide )

    return consensus_sequence
        

def execute():
    fasta = readinput( "c:/downloads/rosalind_cons.txt" )
    cons = calculate_consensus( fasta )
    print( "\nConsensus Genome:" )
    print( ''.join( map( str, cons ) ) )

if __name__ == '__main__':
    start_time = clock()
    execute()
    print( "Total time=%.3f second(s)" % (clock()-start_time) )
