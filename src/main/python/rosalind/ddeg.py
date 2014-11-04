"""
Solution to Rosalind challenge "DDEG: Double-Degree Array"
See http://rosalind.info/problems/ddeg for details.

Given:

    A simple graph with n≤10^3 vertices in the edge list format.

Return:

    An array D[1..n] where D[i] is the sum of the degrees of i's neighbors.
    
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

def dump_graph(graph):
    """
    Print contents of graph object for debugging purposes.
    """
    print( "Dump of graph:")
    for v in graph.items():
        print( "\t" + str(v[0]) + " " + str(v[1]))

def build_undirected_graph( nvertices, edges ):
    """
    In: Edge data in list form.
    Out: Undirected graph of verticies as a list. Each vertex has a list
         of other vertices it is connected to.
    """
    graph = dict()
    for i in range( nvertices ):
        graph[i+1] = []
    for e in edges:
        v1 = e[0]
        v2 = e[1]
        if v1 not in graph: raise Exception("Vertex not in graph")
        if v2 not in graph: raise Exception("Vertex not in graph")
        graph[v1].append(v2)
        graph[v2].append(v1)
    return graph

def sum_neighbors(vertex, graph):
    """
    """
    sum = 0
    for i in vertex[1]:
        sum += len(graph[i])
    return sum

def execute():
    """
    """
    nvertices,nedges,edges = readinput( "c:/downloads/rosalind_ddeg.txt" )
    if nedges != len(edges):
        raise Exception( "Edge count mismatch" )
    print( "nvertices=%d nedges=%d" % (nvertices,nedges))
    graph = build_undirected_graph( nvertices,edges )
    sums = []
    for v in graph.items():
        sum = sum_neighbors(v,graph)
        sums.append( sum )
    print( ' '.join(map(str,sums)))

if __name__ == "__main__":
    start_time = clock()
    execute()
    print( "Total time=%.3f second(s)" % (clock()-start_time) )
