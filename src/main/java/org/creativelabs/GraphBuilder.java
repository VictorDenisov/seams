package org.creativelabs;

public interface GraphBuilder {
    Vertex addVertex(String label);

    void addEdge(Vertex from, Vertex to);
}
