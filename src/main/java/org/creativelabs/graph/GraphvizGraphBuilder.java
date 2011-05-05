package org.creativelabs.graph;

import org.creativelabs.graph.condition.Condition;

import java.io.PrintWriter;
import java.util.*;

public class GraphvizGraphBuilder implements GraphBuilder {

    private static class GraphvizVertex implements Vertex, Comparable<GraphvizVertex> {

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
            return (label + "[" +
                    internalCondition.getStringRepresentation() + " | " +
                    externalCondition.getStringRepresentation() + "]").
                    replace("\"", "'");
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
        public int compareTo(GraphvizVertex o) {
            return this.getLabel().compareTo(o.getLabel());
        }
    }

    private Map<GraphvizVertex, ArrayList<GraphvizVertex>> edgesMap
            = new TreeMap<GraphvizVertex, ArrayList<GraphvizVertex>>();

    private Set<GraphvizVertex> vertexes = new TreeSet<GraphvizVertex>();

    private PrintWriter printWriter;

    public GraphvizGraphBuilder(PrintWriter printWriter) {
        this.printWriter = printWriter;
        printWriter.println("digraph G {");
        printWriter.println("    size=\"120,60\"");
        printWriter.println("    ratio=fill");
    }

    @Override
    public Vertex addVertex(String label, Condition internalCondition, Condition externalCondition) {
        GraphvizVertex vertex = new GraphvizVertex(label, internalCondition, externalCondition);
        vertexes.add(vertex);
        return vertex;
    }

    @Override
    public void addEdge(Vertex from, Vertex to) {
        ArrayList<GraphvizVertex> vertexes;
        if (edgesMap.containsKey(from)) {
            vertexes = edgesMap.get(from);
        } else {
            vertexes = new ArrayList<GraphvizVertex>();
            edgesMap.put((GraphvizVertex) from, vertexes);
        }
        vertexes.add((GraphvizVertex) to);

    }

    public void finalizeGraph() {

        for (Map.Entry<GraphvizVertex, ArrayList<GraphvizVertex>> entry : edgesMap.entrySet()) {
            GraphvizVertex key = entry.getKey();
            for (GraphvizVertex value : entry.getValue()) {
                printWriter.println("    \"" + key.getLabel() + "\" -> \"" + value.getLabel() + "\";");
            }
        }

        Set<GraphvizVertex> edgeVertexes = new HashSet<GraphvizVertex>();
        for (Map.Entry<GraphvizVertex, ArrayList<GraphvizVertex>> entry : edgesMap.entrySet()) {
            edgeVertexes.add(entry.getKey());
            edgeVertexes.addAll(entry.getValue());
        }

        for (GraphvizVertex vertex : vertexes) {
            if (!edgeVertexes.contains(vertex)) {
                printWriter.println("    \"" + vertex.getLabel() + "\";");
            }
        }

        printWriter.println("}");
    }

}
