package org.creativelabs.iig;

import org.creativelabs.graph.*;
import org.creativelabs.graph.edge.condition.EdgeCondition;
import org.creativelabs.graph.edge.condition.EmptyEdgeCondition;

import java.util.*;

/**
 * It's internal instances graph without edge conditions.
 */
public class SimpleInternalInstancesGraph implements InternalInstancesGraph {
    private List<String> fromVertexes = new ArrayList<String>();
    private List<String> toVertexes = new ArrayList<String>();

    @Override
    public void add(String from, String to) {
        fromVertexes.add(from);
        toVertexes.add(to);
    }

    @Override
    public void add(String from, String to, EdgeCondition condition) {
        fromVertexes.add(from);
        toVertexes.add(to);
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
            map.put(vertex, graphBuilder.addVertex(vertex));
        }

        for (int i = 0; i < fromVertexes.size(); i++) {
            Vertex a = map.get(fromVertexes.get(i));
            Vertex b = map.get(toVertexes.get(i));
            graphBuilder.addEdge(a, b, new EmptyEdgeCondition());
        }
    }
}
