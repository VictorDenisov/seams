package org.creativelabs.graph;

import org.creativelabs.graph.edge.condition.EdgeCondition;
import org.creativelabs.graph.edge.condition.EmptyEdgeCondition;

import java.io.*;

public class GraphvizGraphBuilder implements GraphBuilder {

    private static class GraphvizVertex implements Vertex {

        private String label;

        public GraphvizVertex(String label) {
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

    private PrintWriter printWriter;

    public GraphvizGraphBuilder(PrintWriter printWriter) {
        this.printWriter = printWriter;
        printWriter.println("digraph G {");
        printWriter.println("    size=\"60,30\"");
        printWriter.println("    ratio=fill");
    }

    @Override
    public Vertex addVertex(String label) {
        return new GraphvizVertex(label);
    }

    @Override
    public void addEdge(Vertex from, Vertex to, EdgeCondition condition) {
        if (condition instanceof EmptyEdgeCondition) {
            printWriter.println("    " + from.getLabel() + " -> " + to.getLabel() + ";");
        } else {
            printWriter.println("    " + from.getLabel() + " -> " + to.getLabel() + " [label = \"" +
                    condition.getStringRepresentation() + "\"];");
        }
    }

    public void finalizeGraph() {
        printWriter.println("}");
    }

}
