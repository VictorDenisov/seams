package org.creativelabs.graph;

import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.*;
import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.EmptyCondition;

public class JungGraphBuilder implements GraphBuilder {

    private Graph<Vertex, String> graph = new SparseMultigraph<Vertex, String>();

    private static class JungVertex implements Vertex {

        private String label;
        private Condition internalCondition;
        private Condition externalCondition;

        private JungVertex(String label, Condition internalCondition, Condition externalCondition) {
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

    }

    @Override
    public Vertex addVertex(String label, Condition internalCondition, Condition externalCondition) {
        JungVertex vertex = new JungVertex(label, internalCondition, externalCondition);
        graph.addVertex(vertex);
        return vertex;
    }

    public void addEdge(Vertex from, Vertex to, Condition condition) {
        graph.addVertex(from);
        if (condition instanceof EmptyCondition) {
            graph.addEdge(from.getLabel() + " -- " + to.getLabel(), from, to, EdgeType.DIRECTED);
        } else {
            graph.addEdge(condition.getStringRepresentation(), from, to, EdgeType.DIRECTED);
        }
    }

    public Graph<Vertex, String> getGraph() {
        return graph;
    }
}
