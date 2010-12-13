package org.creativelabs;

interface GraphBuilder {
    Vertex addVertex(String label);

    void addEdge(Vertex from, Vertex to);
}
