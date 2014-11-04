"""
Solution to Rosalind challenge "CC: Connected Components"
See http://rosalind.info/problems/cc for details.

Given:

    A simple graph with n≤10^3 vertices in the edge list format.

Return:

    The number of connected components in the graph.
    
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
    graph = dict()
    for v in range( nvertices ):
        graph[v+1] = set()
    for e in edges:
        v1 = e[0]
        v2 = e[1]
        if v1 not in graph: raise Exception("Vertex not in graph")
        if v2 not in graph: raise Exception("Vertex not in graph")
        s = graph[v1]
        s.add( v2 )
        graph[v1] = s
        s = graph[v2]
        s.add( v1 )
        graph[v2] = s
    return graph

def dfs(graph, start):
    # borrowed from: http://eddmann.com/posts/depth-first-search-and-breadth-first-search-in-python/
    visited, stack = set(), [start]
    while stack:
        vertex = stack.pop()
        if vertex not in visited:
            visited.add(vertex)
            stack.extend(graph[vertex] - visited)
    return visited

def execute():
    """
    """
    nvertices,nedges,edges = readinput( "c:/downloads/rosalind_cc.txt" )
    if nedges != len(edges):
        raise Exception( "Edge count mismatch" )
    print( "nvertices=%d nedges=%d" % (nvertices,nedges))
    graph = build_undirected_graph( nvertices,edges )
    #dump_graph( graph )
    nodes_list = []
    for v in graph.items():
        nodes = dfs( graph, v[0] )
        nodes_in_list = False
        for n in nodes_list:
            if nodes == n:
                nodes_in_list = True
        if not nodes_in_list:
            nodes_list.append( nodes )
    print( str(len(nodes_list)))
                

if __name__ == "__main__":
    start_time = clock()
    execute()
    print( "Total time=%.3f second(s)" % (clock()-start_time) )
