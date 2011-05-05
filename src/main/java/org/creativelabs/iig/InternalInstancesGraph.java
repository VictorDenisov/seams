package org.creativelabs.iig;

import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.GraphBuilder;

import java.util.Set;

/**
 * @author azotcsit
 * Date: 09.04.11
 * Time: 16:37
 */
public interface InternalInstancesGraph {
    void addEdge(String source, String target);
    //TODO to implement vertexes without edges
    void addVertexConditions(String vertex, Condition internalCondition, Condition externalCondition);
    Condition getInternalVertexCondition(String vertex);
    Condition getExternalVertexCondition(String vertex);
    boolean contains(String variable);
    Set<String> toSet();
    void buildGraph(GraphBuilder graphBuilder);
}
