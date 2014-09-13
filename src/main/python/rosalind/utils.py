"""
A class to hold a bunch of simple, commonly-used utility functions.
"""
class Utils(object):
    def __init__(self):
        pass

    def revcomp( self, seq ):
        """
        Produce a reverse complement of the sequence.
        """
        if 'U' in seq and not 'T' in seq:
            return self.reverse( self.complement_rna( seq ) )
        elif 'T' in seq and not 'U' in seq:
            return self.reverse( self.complement_dna( seq ) )

    def complement_dna( self, dna ):
        """
        Produce a complement of the DNA sequence.
        """
        comp = ''
        for i in range(len(dna)):
            if   dna[i] == 'T': comp += 'A'
            elif dna[i] == 'A': comp += 'T'
            elif dna[i] == 'C': comp += 'G'
            elif dna[i] == 'G': comp += 'C'
        return comp

    def complement_rna( self, rna ):
        """
        Produce a complement of the RNA sequence.
        """
        comp = ''
        for i in range(len(rna)):
            if   rna[i] == 'U': comp += 'A'
            elif rna[i] == 'A': comp += 'U'
            elif rna[i] == 'C': comp += 'G'
            elif rna[i] == 'G': comp += 'C'
        return comp

    def reverse( self, seq ):
        """
        Produce a reversal of the sequence.
        """
        return seq[::-1]
    
