"""
Solution to Rosalind challenge "DEG: Degree Array"
See http://rosalind.info/problems/deg for details.

Given:

    A simple graph with n≤10^3 vertices in the edge list format.

Return:

    An array D[1..n] where D[i] is the degree of vertex i.
    
Author:

    Cary Scofield
    carys689 <at> gmail <dot> com
    
Version: 3.4.1

"""

from time import clock

def readinput( path ):
    """
    In: path of vertex/edge data
    Out: Number of vertices
         Number of edges
         Edges in list form
    """
    with open( path, "r" ) as f:
        nvertices,nedges = [int(c) for c in f.readline().strip().split()]
        edges = []
        while True:
            edge = [int(c) for c in f.readline().strip().split()]
            if len(edge)==0: break
            edges.append( edge )
    return nvertices,nedges,edges

def build_undirected_graph( edges ):
    """
    In: List of edge data
    Out: Graph as a dictionary: each entry is a vertex associated with
    a list of vertices it is connected to.
    """
    graph = dict()
    for e in edges:
        v1 = e[0]
        v2 = e[1]
        if v1 not in graph:
            graph[v1] = []
        graph[v1].append(v2)
        if v2 not in graph:
            graph[v2] = []
        graph[v2].append(v1)
    return graph

def execute():
    nvertices,nedges,edges = readinput( "c:/downloads/rosalind_deg (1).txt" )
    graph = build_undirected_graph( edges )
    output = open("c:/temp/output_deg.txt","w")
    for v in graph.items():
        if len(v[1]) > 1:
            output.write( str(len(v[1])) )
            output.write( " " )
    output.write("\n")

if __name__ == "__main__":
    start_time = clock()
    execute()
    print( "Total time=%.3f second(s)" % (clock()-start_time) )
