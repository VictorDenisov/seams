package org.creativelabs.graph;

import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.*;

public class JungGraphBuilder implements GraphBuilder {

    private Graph<Vertex, String> graph = new SparseMultigraph<Vertex, String>();

    private static class JungVertex implements Vertex {

        private String label;

        public JungVertex(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public Vertex addVertex(String label) {
        JungVertex vertex = new JungVertex(label);
        graph.addVertex(vertex);
        return vertex;
    }

    public void addEdge(Vertex from, Vertex to) {
        graph.addEdge(from.getLabel() + " -- " + to.getLabel(), from, to, EdgeType.DIRECTED);
    }

    public Graph<Vertex, String> getGraph() {
        return graph;
    }
}
