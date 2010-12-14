package org.creativelabs;

import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.*;

import org.creativelabs.ui.jung.JungVertex;

public class JungGraphBuilder implements GraphBuilder {

    private Graph<Vertex, String> graph = new SparseMultigraph<Vertex, String>();

    public Vertex addVertex(String label) {
        JungVertex vertex = new JungVertex(label);
        graph.addVertex(vertex);
        return vertex;
    }

    public void addEdge(Vertex from, Vertex to) {
        graph.addEdge(from.getLabel() + " -- " + to.getLabel(), from, to, EdgeType.DIRECTED);
    }

    public Graph getGraph() {
        return graph;
    }
}
