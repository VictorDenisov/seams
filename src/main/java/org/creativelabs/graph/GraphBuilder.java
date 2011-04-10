package org.creativelabs.graph;

import org.creativelabs.graph.edge.condition.EdgeCondition;

public interface GraphBuilder {
    Vertex addVertex(String label);
    void addEdge(Vertex from, Vertex to, EdgeCondition condition);
}
