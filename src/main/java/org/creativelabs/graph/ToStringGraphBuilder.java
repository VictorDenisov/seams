package org.creativelabs.graph;

import org.creativelabs.graph.edge.condition.EdgeCondition;
import org.creativelabs.graph.edge.condition.EmptyEdgeCondition;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class ToStringGraphBuilder implements GraphBuilder {

    private static final class StringVertex implements Vertex, Comparable<StringVertex> {
        private String label;

        private StringVertex(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public int compareTo(StringVertex vertex) {
            return label.compareTo(vertex.label);
        }
    }

    private Map<StringVertex, ArrayList<StringVertex>> edgesMap
            = new TreeMap<StringVertex, ArrayList<StringVertex>>();
    private Map<StringVertex, ArrayList<EdgeCondition>> conditionsMap
            = new TreeMap<StringVertex, ArrayList<EdgeCondition>>();

    @Override
    public Vertex addVertex(String label) {
        return new StringVertex(label);
    }

    @Override
    public void addEdge(Vertex from, Vertex to, EdgeCondition condition) {
        ArrayList<StringVertex> vertexes = null;
        if (edgesMap.containsKey(from)) {
            vertexes = edgesMap.get(from);
        } else {
            vertexes = new ArrayList<StringVertex>();
            edgesMap.put((StringVertex) from, vertexes);
        }
        vertexes.add((StringVertex) to);

        ArrayList<EdgeCondition> conditions = null;
        if (conditionsMap.containsKey(from)) {
            conditions = conditionsMap.get(from);
        } else {
            conditions = new ArrayList<EdgeCondition>();
            conditionsMap.put((StringVertex) from, conditions);
        }
        conditions.add(condition);
    }

    public String toString() {
        StringBuffer result = new StringBuffer("{");
        for (Map.Entry<StringVertex, ArrayList<StringVertex>> entry : edgesMap.entrySet()) {
            StringVertex key = entry.getKey();
            for (int i = 0; i < entry.getValue().size(); i++) {
                if (conditionsMap.get(key).get(i) instanceof EmptyEdgeCondition) {
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
