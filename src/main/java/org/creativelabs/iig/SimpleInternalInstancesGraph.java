package org.creativelabs.iig;

import org.creativelabs.graph.*;
import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.EmptyCondition;

import java.util.*;

/**
 * It's internal instances graph without edge conditions.
 */
public class SimpleInternalInstancesGraph implements InternalInstancesGraph {
    private List<String> fromVertexes = new ArrayList<String>();
    private List<String> toVertexes = new ArrayList<String>();

    @Override
    public void addEdge(String from, String to) {
        fromVertexes.add(from);
        toVertexes.add(to);
    }

    @Override
    public void addVertexConditions(String vertex, Condition internalCondition, Condition externalCondition) {
        //no operations
    }

    @Override
    public Condition getInternalVertexCondition(String vertex) {
        return new EmptyCondition();
    }

    @Override
    public Condition getExternalVertexCondition(String vertex) {
        return new EmptyCondition();
    }

    public boolean contains(String variable) {
        return fromVertexes.contains(variable);
    }

    public Set<String> toSet() {
        return new HashSet<String>(fromVertexes);
    }

    public void buildGraph(GraphBuilder graphBuilder) {
        Set<String> vertexes = new HashSet<String>();
        Map<String, Vertex> map = new HashMap<String, Vertex>();

        vertexes.addAll(fromVertexes);
        vertexes.addAll(toVertexes);
        for (String vertex : vertexes) {
            map.put(vertex, graphBuilder.addVertex(vertex, new EmptyCondition(), new EmptyCondition()));
        }

        for (int i = 0; i < fromVertexes.size(); i++) {
            Vertex a = map.get(fromVertexes.get(i));
            Vertex b = map.get(toVertexes.get(i));
            graphBuilder.addEdge(a, b);
        }
    }
}
