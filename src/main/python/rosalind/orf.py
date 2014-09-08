"""
Solution to Rosalind challenge "ORF: Open Reading Frames"
See http://rosalind.info/problems/orf for details.

Attention ROSALIND competitor: if you have not solved this particular
problem yet, it would be unfair to the other competitoris if you
peruse this code, so please refrain from doing so. Well, okay, you
can take a "quick peek" if you're really stuck, but don't copy the code.

Given:

    A DNA string s of length at most 1 kbp in FASTA format.

Return:

    Every distinct candidate protein string that can be translated
    from ORFs of s. Strings can be returned in any order.

Author:

    Cary Scofield
    carys689 <at> gmail <dot> com

"""

from fasta import *
from rna_codon_table import *

START_CODON = 'AUG'
STOP_CODON = '-'

rna_codon_table = RNA_codon_table()

def transcribe( dna ):
    """
    Transcribe a sequence of DNA into RNA.
    """
    rna = ''
    for i in range(len(dna[:])):
        if dna[i] == 'T':
            rna += 'U'
        else:
            rna += dna[i]
    return rna

def complement_rna( seq ):
    """
    Complement an RNA strand.
    """
    comp = ''
    rev = seq[::-1] # reverse the strand
    for i in range(len(rev)):
        if rev[i] == 'U':
            comp += 'A'
        elif rev[i] == 'A':
            comp += 'U'
        elif rev[i] == 'C':
            comp += 'G'
        elif rev[i] == 'G':
            comp += 'C'
        else:
            raise Exception( "Unrecognized nucleotide character: " + rev[i] )
    return comp;

def complement_dna( seq ):
    """
    Complement a DNA strand.
    """
    comp = ''
    rev = seq[::-1] # reverse the strand
    for i in range(len(rev)):
        if rev[i] == 'T':
            comp += 'A'
        elif rev[i] == 'A':
            comp += 'T'
        elif rev[i] == 'C':
            comp += 'G'
        elif rev[i] == 'G':
            comp += 'C'
        else:
            raise Exception( "Unrecognized nucleotide character: " + rev[i] )
    return comp;

def print_candidates(candidates):
    """
    Enumerate the elements of candidates set and print each one found.
    """
    cands = candidates.copy()
    while True:
        try:
            cand = cands.pop()
            print( cand )
        except KeyError:
            return

def get_orf( seq ):
    """
    Scan the sequence looking for a start codon. If found,
    create orf strand until a stop codon is found and
    return the orf strand. If no start codon is found,
    return None.
    """
    orf = ''
    if seq[0:3] == START_CODON:
        orf = rna_codon_table.get_code(START_CODON)
        for i in range( 3, len(seq[3:]), 3 ):
            code = rna_codon_table.get_code(seq[i:i+3])
            if code == STOP_CODON:
                return orf
            else:
                orf += code
    else:
        return None

def get_protein_candidates( seq ):
    """
    Scan sequence looking for open reading frames
    (which are protein candidates). To avoid duplicates,
    we place candidates into a set and return the set.
    """
    cand = set()
    for i in range( len( seq ) ):
        orf = get_orf( seq[i:] )
        if orf != None:
            cand.add( orf )
    return cand
    

def execute():
    f = Fasta( "rosalind_orf.txt" )
    dna = f.get_segments()[0].get_sequence()
    
    rna_5to3 = transcribe( dna )
    
    candidate_proteins = get_protein_candidates( rna_5to3 )
            
    rna_3to5 = transcribe( complement_dna( dna ) )
    
    assert rna_5to3 == complement_rna(rna_3to5), \
           'Two RNA strands are not complementary'
    
    candidate_proteins |= get_protein_candidates( rna_3to5 ) # set union
    print_candidates( candidate_proteins )

if __name__ == '__main__':
    execute()
