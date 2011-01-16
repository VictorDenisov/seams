package org.creativelabs.graph;

import java.util.*;

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

    private TreeMap<StringVertex, ArrayList<StringVertex>> map 
        = new TreeMap<StringVertex, ArrayList<StringVertex>>();

    public Vertex addVertex(String label) {
        return new StringVertex(label);
    }

    public void addEdge(Vertex from, Vertex to) {
        ArrayList<StringVertex> list = null;
        if (map.containsKey(from)) {
            list = map.get(from);
        } else {
            list = new ArrayList<StringVertex>();
            map.put((StringVertex) from, list);
        }
        list.add((StringVertex) to);
    }

    public String toString() {
        StringBuffer result = new StringBuffer("{");
        for (Map.Entry<StringVertex, ArrayList<StringVertex>> entry : map.entrySet()) {
            for (StringVertex to : entry.getValue()) {
                result.append(entry.getKey().getLabel() + " -> " + to.getLabel() + ", ");
            }
        }
        return result.toString() + "}";
    }

}
