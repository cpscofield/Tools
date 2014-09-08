"""
Solution to Rosalind challenge "MPRT: Finding a Protein Motif"
See http://rosalind.info/problems/mprt for details.

Attention ROSALIND competitor: if you have not solved this particular
problem yet, it would be unfair to all the other competitoris if you
peruse this code, so please refrain from doing so.

Given:

    At most 15 UniProt Protein Database access IDs. Example input:

        Q00001_RHGA_ASPAC
        P01044_KNH1_BOVIN
        B5FPF2
        Q9D9T0
        P03415_VME1_CVMA5
        P10493_NIDO_MOUSE
        P21809_PGS1_BOVIN
        A8G1M9
        P01215_GLHA_HUMAN
        P01233_CGHB_HUMAN
        P02748_CO9_HUMAN
        B6DCT5
        Q05865
        P08709_FA7_HUMAN
        P28314_PER_COPCI

Return:

    For each protein possessing the N-glycosylation motif,
    output its given access ID followed by a list of locations
    in the protein string where the motif can be found.

Author:

    Cary Scofield
    carys689 <at> gmail <dot> com

"""

from urllib import request

URLBASE = "http://www.uniprot.org/uniprot/"

def get_fasta( url ):
    """
    Get FASTA data from web site designated by UNIPROT url.
    """
    try:
        urlstr = url + ".fasta"
        u = request.urlopen( urlstr )
        response = u.read()
        return response
    except URLError as e:
        print( e )
    except Exception as e:
        print( "Exception caught: " + e )
        return None

def join_list(l, sep=''):
    """
    Join list of strings into a single string each separated by sep.
    """
    s = ''
    for i in range(len(l)):
        if l[i] != '':
            s += l[i] + sep
    return s

def get_sequence( fasta ):
    """
    Extract the amino acid sequence from FASTA data as a single string.
    """
    lines = fasta.decode("utf-8").split( '\n')
    return join_list(lines[1:]).upper() # Ignore FASTA header

def motif_found( sequence ):
    """
    Does sequence match this pattern: N{P}[ST]{P} 
    """
    if len(sequence) >= 4:
        if sequence[0] == 'N':
            if sequence[1] != 'P':
                if sequence[2] == 'S' or sequence[2] == 'T':
                    if sequence[3] != 'P':
                        return True
    return False

def find_motif_locations( sequence ):
    """
    Cycle through each position of the sequence, testing for
    existence of protein motif.
    """
    indices = []
    for i in range(len(sequence)):
        if motif_found( sequence[i:] ):
            indices.append(str(i + 1)) # 1-based not 0-based!!
            i += 4 # skip past end of protein motif
    return join_list( indices, sep=' ' )
    
def execute():
    f = open( "rosalind_mprt.txt", "r" )
    line = f.readline().rstrip()
    while line:
        fasta = get_fasta( URLBASE + line )
        sequence = get_sequence( fasta )
        result = find_motif_locations(sequence)
        if( result != '' ):
            print( line )
            print( result )
        line = f.readline().rstrip()
    f.close()

if __name__ == '__main__':
    execute()
        
