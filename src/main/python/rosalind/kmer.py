# -*- coding: utf-8 -*-
"""
Solution to Rosalind challenge "KMER: k-Mer Composition"
See http://rosalind.info/problems/kmer for details.

Given:

    A DNA string s in FASTA format (having length at most 100 kbp).

Return:

    The 4-mer composition of s.
    
Author:

    Cary Scofield
    carys689 <at> gmail <dot> com

"""

from itertools import product
from functools import cmp_to_key
from fasta import Fasta

table = dict()
N = 4

def readinput( path ):
    fasta = Fasta( path )
    seq = fasta.get_segments()[0].get_sequence()
    return seq

def compare(s1,s2):
    """
    """
    for i in range(len(s1)):
        diff = table[s1[i]] - table[s2[i]]
        if diff == 0: continue
        return diff
    return 0

def build_sort_table(symbols,n):
    """
    Build table that dictates lexicographical ordering.
    """
    return dict((k,s) for s,k in enumerate(symbols))
    
def sort_it( symbols, n ):
    """
    Sort the symbols.
    """
    return sorted(product(symbols,repeat=n),key=cmp_to_key(compare))

def generate_kmers( sorted_data ):
    """
    Generate the k-mers.
    """
    k_mers = []
    for i in range(len(sorted_data)):
        s = ''
        for j in range(len(sorted_data[i])):
            s += sorted_data[i][j]
        k_mers.append( s )
    return k_mers

def generate_kmer_composition( sequence, k_mers ):
    """
    Generate the K-mer composition of the DNA sequence.
    """
    comp = ''
    for i in range(len(k_mers )):
        count = 0
        for j in range(len(sequence)):
            if k_mers[i] == sequence[j:j+4]:
                count += 1
        if i > 0: comp += ' '
        comp += str(count)
    return comp

def execute():
    sequence = readinput("rosalind_kmer.txt")
    global table
    symbols = 'ACGT'
    table = build_sort_table(symbols, N)
    sorted_data = sort_it(symbols,N)
    k_mers = generate_kmers( sorted_data )
    print( generate_kmer_composition( sequence, k_mers ) )    
            
if __name__ == '__main__':
    execute()
