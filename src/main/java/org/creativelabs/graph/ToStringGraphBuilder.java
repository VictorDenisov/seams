package org.creativelabs.graph;

import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.EmptyCondition;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

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
    private Map<StringVertex, ArrayList<Condition>> conditionsMap
            = new TreeMap<StringVertex, ArrayList<Condition>>();

    @Override
    public Vertex addVertex(String label, Condition internalCondition, Condition externalCondition) {
        return new StringVertex(label, internalCondition, externalCondition);
    }

    @Override
    public void addEdge(Vertex from, Vertex to, Condition condition) {
        ArrayList<StringVertex> vertexes;
        if (edgesMap.containsKey(from)) {
            vertexes = edgesMap.get(from);
        } else {
            vertexes = new ArrayList<StringVertex>();
            edgesMap.put((StringVertex) from, vertexes);
        }
        vertexes.add((StringVertex) to);

        ArrayList<Condition> conditions = null;
        if (conditionsMap.containsKey(from)) {
            conditions = conditionsMap.get(from);
        } else {
            conditions = new ArrayList<Condition>();
            conditionsMap.put((StringVertex) from, conditions);
        }
        conditions.add(condition);
    }

    public String toString() {
        StringBuffer result = new StringBuffer("{");
        for (Map.Entry<StringVertex, ArrayList<StringVertex>> entry : edgesMap.entrySet()) {
            StringVertex key = entry.getKey();
            for (int i = 0; i < entry.getValue().size(); i++) {
                if (conditionsMap.get(key).get(i) instanceof EmptyCondition) {
                    result.append(key.getLabel() + " -> " + entry.getValue().get(i).getLabel() + ", ");
                } else {
                    result.append(key.getLabel() + " -> " + entry.getValue().get(i).getLabel()
                            + " [" + conditionsMap.get(key).get(i).getStringRepresentation() + "], ");
                }
            }
        }
        return result.toString() + "}";
    }

}
