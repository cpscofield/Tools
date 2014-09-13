"""
Solution to Rosalind challenge "TRAN: Transitions and Transversions"
See http://rosalind.info/problems/tran for details.

Given:

    Two DNA strings s1 and s2 of equal length (at most 1 kbp).

Return:

    The transition/transversion ratio R(s1,s2).
    
Author:

    Cary Scofield
    carys689 <at> gmail <dot> com

"""

from fasta import Fasta

def is_transition( c1, c2 ):
    return c1 == 'A' and c2 == 'G' or \
       c1 == 'G' and c2 == 'A' or \
       c1 == 'C' and c2 == 'T' or \
       c1 == 'T' and c2 == 'C'

def compute_ratio( s1, s2 ):
    transitions = 0
    transversions = 0
    for i in range(len(s1)):
        if s1[i] == s2[i]:
            continue
        elif is_transition( s1[i], s2[i] ):
            transitions += 1
        else:
            transversions += 1
    return float(transitions)/float(transversions)

def execute():
    fasta = Fasta( "rosalind_tran.txt" )
    s1 = fasta.get_segments()[0].get_sequence().upper()
    s2 = fasta.get_segments()[1].get_sequence().upper()
    print( "s1",s1 )
    print( "s2",s2)
    ratio = compute_ratio( s1, s2 )
    print( "ratio=" + str(ratio) )

if __name__ == '__main__':
    execute()
