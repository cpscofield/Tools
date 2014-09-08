"""
Solution to Rosalind challenge "MRNA: Inferring mRNA from Protein"
See http://rosalind.info/problems/mrna for details.

Attention ROSALIND competitor: if you have not solved this particular
problem yet, it would be unfair to all the other competitoris if you
peruse this code, so please refrain from doing so.

Given:

    A protein string of length at most 1000 aa.

Return:

    The total number of different RNA strings from which
    the protein could have been translated, modulo 1,000,000.
    (Don't neglect the importance of the stop codon in protein translation.)
    
Author:

    Cary Scofield
    carys689 <at> gmail <dot> com

"""

from rna_codon_table import RNA_codon_table


def get_sequence( f ):
    """
    Get amino acid sequence from text file f.
    """
    sequence = ''
    line = f.readline().rstrip()
    while line:
        sequence += line
        line = f.readline().rstrip()
    return sequence

def execute():
    datafile = open( "rosalind_mrna.txt", "r")
    sequence = get_sequence( datafile )
    datafile.close()
    aa_table = RNA_codon_table()
    print( sequence )
    tallies = aa_table.get_tallies()
    print(tallies)
    p = 1
    for s in sequence:
        try:
            p *= tallies[s]
        except KeyError:
            print( "Unable to find amino acid " + s + " in tally table" )
    print( ( p * tallies['-'] ) % 1000000 )
    
if __name__ == '__main__':
    execute()
