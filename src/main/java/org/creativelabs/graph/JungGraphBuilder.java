package org.creativelabs.graph;

import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.creativelabs.graph.condition.Condition;

public class JungGraphBuilder implements GraphBuilder {

    private Log log = LogFactory.getLog(JungGraphBuilder.class);

    //    private Graph<Vertex, String> graph = new SparseMultigraph<Vertex, String>();
    private Graph<Vertex, String> graph = new DelegateForest<Vertex, String>();

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

        @Override
        public String toString() {
            return "JungVertex{" +
                    "label='" + label + '\'' +
                    ", internalCondition=" + internalCondition +
                    ", externalCondition=" + externalCondition +
                    '}';
        }
    }

    @Override
    public Vertex addVertex(String label, Condition internalCondition, Condition externalCondition) {
        JungVertex vertex = new JungVertex(label, internalCondition, externalCondition);
        graph.addVertex(vertex);
        return vertex;
    }

    public void addEdge(Vertex from, Vertex to) {
        try {
            graph.removeVertex(to);
            if (!graph.getVertices().contains(from)) {
                graph.addVertex(from);
            }
            if (!from.equals(to)) {
                graph.addEdge(from.getLabel() + " -- " + to.getLabel(), from, to, EdgeType.DIRECTED);
            }
        } catch (Throwable t) {
            log.error("Couldn't add vertex or edge to graph.", t);
        }
    }

    public Graph<Vertex, String> getGraph() {
        return graph;
    }
}
