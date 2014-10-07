"""
A rudimentary class for reading a FASTA file. By "rudimentary", we mean we do
not parse the FASTA header except to get whatever is in the header as a single
string. Other than that we, of course, get all the genomic sequence data that
is included in each and every FASTA 'segment' and collapse it into a single
contiguous string.

Author: Cary Scofield, carys689 <at> gmail <dot> com

"""
class Fasta(object):
    def __init__(self, path):
        """
        Constructor: open the FASTA file, read and store all the FASTA
        segments (each one delimited by a header).
        """
        self._file = open( path, "r")
        line = self._file.readline()
        header = ''
        sequence = []
        self._segments = [] # List of FastaSegment objects
        
        while line:
            if line[0] == '>':
                if len(sequence) > 0:
                    self._segments.append( FastaSegment( header, sequence ) )
                header = line[1:].rstrip()
                sequence = []
            else:
                sequence.append( line.rstrip().upper().replace( ' ', '' ) )
            line = self._file.readline()
            
        self._file.close()
        self._segments.append( FastaSegment( header, sequence ) )

    def get_segments(self):
        """
        Get all the segments found in the FASTA file as a list.
        """
        return self._segments

"""
Class to retain the data for a FASTA segment (i.e., header and sequence data).
"""
class FastaSegment(object):
    def __init__(self, header, sequence):
        """
        Constructor.
        """
        self._header = header # fasta_header
        self._sequence = sequence

    def get_header(self):
        """
        Return the header.
        """
        return self._header

    def get_sequence(self):
        """
        Return the sequence data.
        """
        return self._join_list( self._sequence, '' )

    def _join_list( self, l, sep='' ):
        """
        Internal method to join a list of strings found in a list
        each separated by sep.
        """
        s = ''
        for i in range(len(l)):
            s += l[i].strip() + sep
        return s
    
"""
Class to retain information about a FASTA header.
"""
class FastaHeader(object):
    def __init__(self, header):
        """
        Constructor.
        """
        if '|' in header:
            """ TODO: Need to parse header components. """
            self._name = header # for now just copy all the components into name
        else:
            self._name = header # for now just a string containing a name

    def get_name(self):
        """
        Get the name of the FASTA segment.
        """
        return self._name

def run_tests():

    print( "Running tests...")
    seg = []
    seg.append( FastaSegment( 'Fasta_hdr1', 'TGAACTGGATTT\nTCCAATTGGCCGG' ))
    seg.append( FastaSegment( 'Fasta_hdr2', 'GGAACACATT' ))
    try:
        assert seg[0].get_sequence()=='TGAACTGGATTTTCCAATTGGCCGG', 'Wrong sequence returned: ' + seg[0].get_sequence()
        assert seg[0].get_header()=='Fasta_hdr1', 'Wrong header returned: ' + seg[0].get_header()
        assert seg[1].get_sequence()=='GGAACACATT', 'Wrong sequence returned: ' + seg[1].get_sequence()
        assert seg[1].get_header()=='Fasta_hdr2', 'Wrong header returned: ' + seg[1].get_header()
    except AssertionError as e:
        print( "Assertion failed: " + str(e) )

    f = Fasta( 'fasta_test_data.txt' )
    seg = f.get_segments()
    try:
        assert len(seg) == 2, 'Wrong number of seqments returned: ' + len(seg)
        assert seg[0].get_sequence() != seg[1].get_sequence(), 'Sequences are not different'
        assert seg[0].get_sequence() == seg[1].get_sequence()[::-1], 'Sequences are not reverses of each other'
        assert seg[0].get_header() == 'Rosalind_99', 'Wrong header returned: '   + seg[0].get_header()
        assert seg[0].get_sequence()[0:4] == 'AGCC', 'Wrong sequence [0] returned: ' + seg[0].get_sequence()[0:4]
        assert seg[0].get_sequence()[-4:] == 'TCAG', 'Wrong sequence [0] returned: ' + seg[0].get_sequence()[-4:]
        assert seg[1].get_header() == 'Rosalind_00', 'Wrong header returned: '   + seg[1].get_header()
        assert seg[1].get_sequence()[0:4] == 'GACT', 'Wrong sequence [1] returned: ' + seg[1].get_sequence()[0:4]
        assert seg[1].get_sequence()[-4:] == 'CCGA', 'Wrong sequence [1] returned: ' + seg[1].get_sequence()[-4:]
    except AssertionError as e:
        print( "Assertion failed: " + str(e) )
        
    print( "Tests completed." )

if __name__ == '__main__':
    run_tests()
