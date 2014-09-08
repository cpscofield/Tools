"""
Class to contain table of monosiotopic mass values.
"""

from math import fabs

class MonoisotopicMassTable( object ):
    def __init__( self ):
        self._table = {
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
            'Y':   163.06333
            }

    def get_mass( self, codon ):
        mass = float(0)
        try:
            mass = self._table[ codon.upper() ]
        except KeyError as e:
            print( str(e) + ": " + codon )
        return mass

def run_tests():
    print( "Running tests...")
    mmt = MonoisotopicMassTable()
    mass = float(0)
    mass += mmt.get_mass( 'A' )
    mass += mmt.get_mass( 'm' )
    mass += mmt.get_mass( 'Q' )
    ANSWER = float(330.13618)
    EPSILON = float(0.000001)
    assert fabs(mass-ANSWER) < EPSILON, "Wrong answer: " + mass
    print( "Tests completed." )
    
if __name__ == '__main__':
    run_tests()
    
    
