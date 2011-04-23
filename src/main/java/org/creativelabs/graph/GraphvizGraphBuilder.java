package org.creativelabs.graph;

import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.EmptyCondition;

import java.io.*;

public class GraphvizGraphBuilder implements GraphBuilder {

    private static class GraphvizVertex implements Vertex {

        private String label;
        private Condition internalCondition;
        private Condition externalCondition;

        private GraphvizVertex(String label, Condition internalCondition, Condition externalCondition) {
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

    private PrintWriter printWriter;

    public GraphvizGraphBuilder(PrintWriter printWriter) {
        this.printWriter = printWriter;
        printWriter.println("digraph G {");
        printWriter.println("    size=\"60,30\"");
        printWriter.println("    ratio=fill");
    }

    @Override
    public Vertex addVertex(String label, Condition internalCondition, Condition externalCondition) {
        return new GraphvizVertex(label, internalCondition, externalCondition);
    }

    @Override
    public void addEdge(Vertex from, Vertex to, Condition condition) {
        if (condition instanceof EmptyCondition) {
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
