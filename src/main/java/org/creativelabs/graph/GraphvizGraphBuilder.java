package org.creativelabs.graph;

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

    public Vertex addVertex(String label) {
        return new GraphvizVertex(label);
    }

    public void addEdge(Vertex from, Vertex to) {
        printWriter.println("    " + from.toString() + " -> " + to.toString() + ";");
    }

    public void finalizeGraph() {
        printWriter.println("}");
    }

}
