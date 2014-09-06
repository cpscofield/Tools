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

RNA_codon_table = {
# U
'UUU': 'F', 'UCU': 'S', 'UAU': 'Y', 'UGU': 'C', # UxU
'UUC': 'F', 'UCC': 'S', 'UAC': 'Y', 'UGC': 'C', # UxC
'UUA': 'L', 'UCA': 'S', 'UAA': '-', 'UGA': '-', # UxA
'UUG': 'L', 'UCG': 'S', 'UAG': '-', 'UGG': 'W', # UxG
# C
'CUU': 'L', 'CCU': 'P', 'CAU': 'H', 'CGU': 'R', # CxU
'CUC': 'L', 'CCC': 'P', 'CAC': 'H', 'CGC': 'R', # CxC
'CUA': 'L', 'CCA': 'P', 'CAA': 'Q', 'CGA': 'R', # CxA
'CUG': 'L', 'CCG': 'P', 'CAG': 'Q', 'CGG': 'R', # CxG
# A
'AUU': 'I', 'ACU': 'T', 'AAU': 'N', 'AGU': 'S', # AxU
'AUC': 'I', 'ACC': 'T', 'AAC': 'N', 'AGC': 'S', # AxC
'AUA': 'I', 'ACA': 'T', 'AAA': 'K', 'AGA': 'R', # AxA
'AUG': 'M', 'ACG': 'T', 'AAG': 'K', 'AGG': 'R', # AxG
# G
'GUU': 'V', 'GCU': 'A', 'GAU': 'D', 'GGU': 'G', # GxU
'GUC': 'V', 'GCC': 'A', 'GAC': 'D', 'GGC': 'G', # GxC
'GUA': 'V', 'GCA': 'A', 'GAA': 'E', 'GGA': 'G', # GxA
'GUG': 'V', 'GCG': 'A', 'GAG': 'E', 'GGG': 'G'  # GxG
}

def tally_aa():
    """
    Tally the amino acids in RNA_codon_table and
    return a dictionary of the counts corresponding
    to each amino acid.
    """
    aa_tallies = {}
    for a in RNA_codon_table.values():
        try:
            tally = aa_tallies[a]
            tally += 1
            aa_tallies[a] = tally
        except KeyError:
            aa_tallies[a] = 1
    return aa_tallies
    

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
    print( sequence )
    tallies = tally_aa()
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
