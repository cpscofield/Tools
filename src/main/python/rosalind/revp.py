"""
Solution to Rosalind challenge "REVP: Locating Restriction Sites"
See http://rosalind.info/problems/revp for details.

Given:

    A DNA string of length at most 1 kbp in FASTA format.

Return:

    The position and length of every reverse palindrome
    in the string having length between 4 and 12. You may
    return these pairs in any order.

Author:

    Cary Scofield
    carys689 <at> gmail <dot> com

"""

from fasta import Fasta

def complement( dna ):
    comp = ''
    for i in range(len(dna)):
        if   dna[i] == 'T': comp += 'A'
        elif dna[i] == 'A': comp += 'T'
        elif dna[i] == 'C': comp += 'G'
        elif dna[i] == 'G': comp += 'C'
    return comp

def reverse( s ):
    return s[::-1]

def rev_palindrome( dna ):
    rev = reverse( complement( dna ) )
    return dna == rev

def find_restrictions( dna ):
    restrictions = []
    for i in range(4,13):
        for j in range(len(dna)):
            if i < len(dna)-j+1:
                if rev_palindrome(dna[j:j+i]):
                    restrictions.append( (j+1,i) )
    return restrictions

def execute():
    fasta = Fasta( "rosalind_revp.txt" )
    output = open( "output_revp.txt", "w" )
    dna = fasta.get_segments()[0].get_sequence()
    #print( dna )
    restrictions = find_restrictions( dna )
    for i in range(len(restrictions)):
        print( str(restrictions[i][0])+" "+str(restrictions[i][1]))
        output.write(str(restrictions[i][0])+" "+str(restrictions[i][1]))
        output.write("\n")

if __name__ == '__main__':
    execute()
