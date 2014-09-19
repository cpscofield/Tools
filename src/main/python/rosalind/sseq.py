# -*- coding: utf-8 -*-
"""
Solution to Rosalind challenge "SSEQ: Finding a Spliced Motif"
See http://rosalind.info/problems/sseq for details.

Given:

    Two DNA strings s and t (each of length at most 1 kbp) in FASTA format.

Return:

    One collection of indices of s in which the symbols of t
    appear as a subsequence of s. If multiple solutions exist,
    you may return any one.
    
Author:

    Cary Scofield
    carys689 <at> gmail <dot> com

"""

from fasta import Fasta
from utils import Utils

utils = Utils()

def readinput( path ):
    """
    Read input from FASTA file.
    """
    fasta = Fasta( path )
    segs = fasta.get_segments()
    s = segs[0].get_sequence()
    t = segs[1].get_sequence()
    return s,t

def find_subsequence( s, t ):
    """
    Find subsequence t in s.
    """
    snext = 0
    indices = []
    for i in range(len(t)):
        for j in range(snext,len(s)):
            if t[i] == s[j]:
                indices.append( j+1 ) # 1-based!!
                snext = j + 1 # start next scan on s on next character
                break
    return utils.list2string( indices )
##    sseq = ''
##    for i in range(len(indices)):
##        if i > 0: sseq += ' '
##        sseq += str(indices[i])
##    return sseq

def execute():
    (s,t) = readinput( "rosalind_sseq.txt" )
    print( "s=%s\nt=%s" % (s,t))
    sseq = find_subsequence( s, t )
    print( sseq )
    
            
if __name__ == '__main__':
    execute()
