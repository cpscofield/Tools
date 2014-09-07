"""
Class to retain single-letter codes for 3-letter amino acid codes.
"""
class RNA_codon_table(object):
    def __init__(self):
        self._table = {
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
        self._tallies = None
        
    def __str__(self):
        return self._table

    def __repr__(self):
        return self.__str__

    def get_code(self,aa_triplet):
        """
        Return single-letter code for amino acid code.
        """
        code = ''
        try:
            code = self._table[aa_triplet]
        except KeyError as e:
            code = 'No code for this amino acid: ' + aa_triplet + ": " + e

        return code

    def get_tallies(self):
        """
        Tally up the single-letter codes for the amino acid codes.
        """
        if self._tallies == None:
            self._tallies = {}
            for a in self._table.values():
                try:
                    tally = self._tallies[a]
                    tally += 1
                    self._tallies[a] = tally
                except KeyError:
                    self._tallies[a] = 1
        else:
            pass
        return self._tallies

    def get_tally(self,aa_triplet):
        """
        Get the single-letter tally for the amino acid code.
        """
        self.get_tallies()
        return self._tallies[aa_triplet]

    
