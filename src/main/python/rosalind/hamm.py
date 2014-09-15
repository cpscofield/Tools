"""
Solution to Rosalind challenge "HAMM: Counting Point Mutations"
See http://rosalind.info/problems/hamm for details.

Given: 

    Two DNA strings s and t of equal length (not exceeding 1 kbp).

Return: 

    The Hamming distance dH(s,t).

Author:

    Cary Scofield
    carys689 <at> gmail <dot> com

"""

def hamm_dist(s,t):
    """
    Compute the Hamming distance between two strings.
    """
    hamm_cnt = 0
    for i in range(len(s)):
        if s[i] != t[i]:
            hamm_cnt += 1
    return hamm_cnt    

def execute():
    """
    text_file = open( "rosalind_hamm.txt", "r")
    s = text_file.readline().rstrip()
    t = text_file.readline().rstrip()
    text_file.close()
    """

    from fasta import Fasta

    fasta = Fasta( "rosalind_tran.txt" )
    s = fasta.get_segments()[0].get_sequence().upper()
    t = fasta.get_segments()[1].get_sequence().upper()
    if len(s) != len(t):
        raise Exception( "lengths do not match" )
    
    print( hamm_dist(s,t) )
    

if __name__ == "__main__":
    execute()
