package org.creativelabs.graph;

import org.creativelabs.graph.condition.Condition;

public interface GraphBuilder {
    Vertex addVertex(String label, Condition internalCondition, Condition externalCondition);
    void addEdge(Vertex from, Vertex to, Condition condition);
}
