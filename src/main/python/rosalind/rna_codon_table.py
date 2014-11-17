from math import fabs

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
            'GUG': 'V', 'GCG': 'A', 'GAG': 'E', 'GGG': 'G',  # GxG
            }
        
        self._inverted_table = {
            '-' : ['UAA', 'UAG', 'UGA'],
            'A' : ['GCU', 'GCC', 'GCA', 'GCG'],
            'C' : ['UGU', 'UGC'],
            'D' : ['GAU', 'GAC'],
            'E' : ['GAA', 'GAG'],
            'F' : ['UUU', 'UUC'],
            'G' : ['GGU', 'GGC', 'GGA', 'GGG'],
            'H' : ['CAU', 'CAC'],
            'I' : ['AUU', 'AUC', 'AUA'],
            'K' : ['AAA', 'AAG'],
            'L' : ['UUA', 'UUG', 'CUU', 'CUC', 'CUA', 'CUG'],
            'M' : ['AUG'],
            'N' : ['AAU', 'AAC'],
            'P' : ['CCU', 'CCC', 'CCA', 'CCG'],
            'Q' : ['CAA', 'CAG'],
            'R' : ['CGU', 'CGC', 'CGA', 'CGG', 'AGA', 'AGG'],
            'S' : ['UCU', 'UCC', 'UCA', 'UCG', 'AGU', 'AGC'],
            'T' : ['ACU', 'ACC', 'ACA', 'ACG'],
            'V' : ['GUU', 'GUC', 'GUA', 'GUG'],
            'W' : ['UGG'],
            'Y' : ['UAU', 'UAC'],
            }

        self._codes = ['A','C','D','E','F','G','H','I','K','L','M','N','P','Q','R','S','T','V','W','Y']
        
        self._name_table = {
            '-' : ['STP','STOP'],
            'A' : ['Ala','Alanine'],
            'C' : ['Cys','Cysteine'],
            'D' : ['Asp','Aspartic acid'],
            'E' : ['Glu','Glutamic acid'],
            'F' : ['Phe','Phenylatlanine'],
            'G' : ['Gly','Glycine'],
            'H' : ['His','Histidine'],
            'I' : ['Ile','Isoleucine'],
            'K' : ['Lys','Lysine'],
            'L' : ['Leu','Leucine'],
            'M' : ['Met','Methionine'],
            'N' : ['Asn','Asparagine'],
            'P' : ['Pro','Proline'],
            'Q' : ['Gln','Glutamine'],
            'R' : ['Arg','Arginine'],
            'S' : ['Ser','Serine'],
            'T' : ['Thr','Threonine'],
            'V' : ['Val','Valine'],
            'W' : ['Trp','Tryptophan'],
            'Y' : ['Tyr','Tyrosine'],
            }
    
        self._daltons = {
            'A':   71.03711,
            'C':   103.00919,
            'D':   115.02694,
            'E':   129.04259,
            'F':   147.06841,
            'G':   57.02146,
            'H':   137.05891,
            'I':   113.08406,
            'K':   128.09496,
            'L':   113.08406,
            'M':   131.04049,
            'N':   114.04293,
            'P':   97.05276,
            'Q':   128.05858,
            'R':   156.10111,
            'S':   87.03203,
            'T':   101.04768,
            'V':   99.06841,
            'W':   186.07931,
            'Y':   163.06333,
            }

        self._integer_daltons = {
            'A':   71,
            'C':   103,
            'D':   115,
            'E':   129,
            'F':   147,
            'G':   57,
            'H':   137,
            'I':   113,
            'K':   128,
            'L':   113,
            'M':   131,
            'N':   114,
            'P':   97,
            'Q':   128,
            'R':   156,
            'S':   87,
            'T':   101,
            'V':   99,
            'W':   186,
            'Y':   163,
            }

        self._inverted_integer_daltons = {
            71:   ['A'],
            103:  ['C'],
            115:  ['D'],
            129:  ['E'],
            147:  ['F'],
            57:   ['G'],
            137:  ['H'],
            113:  ['I','L'],
            128:  ['K','Q'],
            131:  ['M'],
            114:  ['N'],
            97:   ['P'],
            156:  ['R'],
            87:   ['S'],
            101:  ['T'],
            99:   ['V'],
            186:  ['W'],
            163:  ['Y'],
            }
        
        self._tallies = None
        
    def __str__(self):
        return self._table

    def __repr__(self):
        return self.__str__

    def is_stop(self, aa_triplet ):
        code = self._table[aa_triplet]
        return code == '-'

    def get_code(self,aa_triplet):
        """
        Return single-letter code for amino acid code.
        """
        code = ''
        try:
            code = self._table[aa_triplet]
        except KeyError as e:
            code = 'No code for this amino acid: ' + aa_triplet + ": " + str(e)

        return code

    def get_codons(self, code ):
        """
        Return list of codons for single-letter code.
        """
        codons = self._inverted_table[code]
        return codons

    def get_all_codes(self):
        """
        Return list of single-letter codes for all amino acids.
        """
        return self._names

    def get_abbrev(self, code ):
        """
        Return abbreviated name for single-letter code.
        """
        return _name_table[code][0]

    def get_name(self, code ):
        """
        return full name for single-letter code.
        """
        return _name_table[code][1]

    def get_mass( self, code ):
        """
        Given a code, return its mass.
        """
        mass = float(0)
        try:
            mass = self._daltons[ code.upper() ]
        except KeyError as e:
            print( str(e) + ": " + code )
        return mass

    def get_integer_mass( self, code ):
        """
        Given a code, return its integer mass.
        """
        return round( self.get_mass( code ) )

    def get_inverted_integer_mass( self, mass ):
        """
        Given an integer mass, return the code(s) associated with it.
        """
        return self._inverted_integer_daltons[mass]

    def get_all_masses( self ):
        """
        Return the masses for all the codes.
        """
        return self._daltons.items()

    def get_all_integer_masses( self ):
        """
        Return the integer masses for all the codes.
        """
        return self._integer_daltons.items()
        pass

    def get_all_inverted_integer_masses( self ):
        """
        
        """
        return self._inverted_integer_daltons
    
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
# end of class RNA_codon_table

table = RNA_codon_table()

def check_inverted_table(code):
    codons = table.get_codons(code)
    for i in codons:
        c = table.get_code( i )
        assert c == code, "code=" + code + " returns wrong codon=" + i;

def run_tests():
    print( "Running tests...")
    for i in ['F','L','I','M','V','S','P','T','A','Y','-',\
              'H','Q','N','K','D','E','C','W','R','G']:
        check_inverted_table(i)
    mass = float(0)
    mass += table.get_mass( 'A' )
    mass += table.get_mass( 'm' )
    mass += table.get_mass( 'Q' )
    ANSWER = float(330.13618)
    EPSILON = float(0.000001)
    assert fabs(mass-ANSWER) < EPSILON, "Wrong answer: " + mass

    mass = 0
    mass += table.get_integer_mass( 'A' )
    mass += table.get_integer_mass( 'm' )
    mass += table.get_integer_mass( 'Q' )
    ANSWER = 330
    assert mass == ANSWER, "Wrong integer answer: " + mass

    print( "Float masses:" )
    print( ' '.join(map(str,table.get_all_masses())))
    print( "Integer masses:" )
    print( ' '.join(map(str,table.get_all_integer_masses())))
    print( "Inverted integer masses:" )
    print( ' '.join(map(str,table.get_all_inverted_integer_masses())))
    
    print( "Tests completed." )
    

if __name__ == '__main__':
    run_tests()
