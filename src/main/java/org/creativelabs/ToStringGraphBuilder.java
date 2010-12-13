package org.creativelabs;

import java.util.*;

class ToStringGraphBuilder implements GraphBuilder {

    private class StringVertex implements Vertex {
        private String label;

        private StringVertex(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    HashMap<Vertex, Vertex> map = new HashMap<Vertex, Vertex> ();

    public Vertex addVertex(String label) {
        return new StringVertex(label);
    }

    public void addEdge(Vertex from, Vertex to) {
        map.put(from, to);
    }

    public String toString() {
        StringBuffer result = new StringBuffer("{");
        for (Map.Entry<Vertex, Vertex> entry : map.entrySet()) {
            result.append(entry.getKey().getLabel() + " -> " + entry.getValue().getLabel() + ", ");
        }
        return result.toString() + "}";
    }

}
