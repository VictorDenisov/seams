package org.creativelabs.graph;

import org.creativelabs.graph.condition.Condition;

import java.util.*;

public class ToStringGraphBuilder implements GraphBuilder {

    private static class StringVertex implements Vertex, Comparable<Vertex> {

        private String label;
        private Condition internalCondition;
        private Condition externalCondition;

        private StringVertex(String label, Condition internalCondition, Condition externalCondition) {
            this.label = label;
            this.internalCondition = internalCondition;
            this.externalCondition = externalCondition;
        }

        @Override
        public String getLabel() {
            return label + "[" +
                    internalCondition.getStringRepresentation() + " | " +
                    externalCondition.getStringRepresentation() + "]";
        }

        @Override
        public Condition getInternalCondition() {
            return internalCondition;
        }

        @Override
        public Condition getExternalCondition() {
            return externalCondition;
        }

        @Override
        public int compareTo(Vertex o) {
            return this.getLabel().compareTo(o.getLabel());
        }
    }

    private Map<StringVertex, ArrayList<StringVertex>> edgesMap
            = new TreeMap<StringVertex, ArrayList<StringVertex>>();

    private Set<StringVertex> vertexes = new TreeSet<StringVertex>();

    @Override
    public Vertex addVertex(String label, Condition internalCondition, Condition externalCondition) {
        StringVertex vertex = new StringVertex(label, internalCondition, externalCondition);
        vertexes.add(vertex);
        return vertex;
    }

    @Override
    public void addEdge(Vertex from, Vertex to) {
        ArrayList<StringVertex> vertexes;
        if (edgesMap.containsKey(from)) {
            vertexes = edgesMap.get(from);
        } else {
            vertexes = new ArrayList<StringVertex>();
            edgesMap.put((StringVertex) from, vertexes);
        }
        vertexes.add((StringVertex) to);
    }

    public String toString() {
        StringBuffer result = new StringBuffer("{");
        for (Map.Entry<StringVertex, ArrayList<StringVertex>> entry : edgesMap.entrySet()) {
            StringVertex key = entry.getKey();
            for (int i = 0; i < entry.getValue().size(); i++) {
                result.append(key.getLabel() + " -> " + entry.getValue().get(i).getLabel() + ", ");
            }
        }

        Set<StringVertex> edgeVertexes = new HashSet<StringVertex>();
        for (Map.Entry<StringVertex, ArrayList<StringVertex>> entry : edgesMap.entrySet()) {
            edgeVertexes.add(entry.getKey());
            edgeVertexes.addAll(entry.getValue());
        }

        for (StringVertex vertex : vertexes) {
            if (!edgeVertexes.contains(vertex)) {
                result.append(vertex.getLabel() + ", ");
            }
        }

        return result.toString() + "}";
    }

}
