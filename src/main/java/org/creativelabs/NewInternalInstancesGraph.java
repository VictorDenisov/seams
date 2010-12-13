package org.creativelabs;

import java.util.*;

class NewInternalInstancesGraph {

    private HashMap<String, String> edges = new HashMap<String, String>();

    void add(String source, String target) {
        edges.put(source, target);
    }

    public String toString() {
        StringBuffer result = new StringBuffer("{");
        for (Map.Entry<String, String> entry : edges.entrySet()) {
            result.append(entry.getKey() + " -> " + entry.getValue() + ",");
        }
        return result.toString() + "}";
    }

    public boolean contains(String variable) {
        return edges.containsKey(variable);
    }

    public Set<String> toSet() {
        return edges.keySet();
    }

    public void buildGraph(GraphBuilder graphBuilder) {
        Set<String> vertexes = new HashSet<String>();
        Map<String, Vertex> map = new HashMap<String, Vertex>();

        vertexes.addAll(edges.keySet());
        vertexes.addAll(edges.values());
        for (String vertex : vertexes) {
            map.put(vertex, graphBuilder.addVertex(vertex));
        }

        for (Map.Entry<String, String> entry : edges.entrySet()) {
            Vertex a = map.get(entry.getKey());
            Vertex b = map.get(entry.getValue());
            graphBuilder.addEdge(a, b);
        }
    }
}
