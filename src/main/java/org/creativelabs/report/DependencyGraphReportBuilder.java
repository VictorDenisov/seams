package org.creativelabs.report;

import org.creativelabs.graph.*;
import org.creativelabs.*;
import java.util.*;

public class DependencyGraphReportBuilder implements ReportBuilder {
    private GraphBuilder graphBuilder;

    private Map<String, Vertex> map = new HashMap<String, Vertex>();

    public DependencyGraphReportBuilder(GraphBuilder graphBuilder) {
        this.graphBuilder = graphBuilder;
    }

    public void setDependencies(String className, Map<String, Set<Dependency>> dependencies) {
        Vertex classVertex = map.get(className);

        if (classVertex == null) {
            classVertex = graphBuilder.addVertex(className);
        }

        for (Map.Entry<String, Set<Dependency>> entry : dependencies.entrySet()) {
            for (Dependency dependency : entry.getValue()) {
                String str = dependency.getType().toString();
                Vertex vertex;
                if (map.containsKey(str)) {
                    vertex = map.get(str);
                } else {
                    vertex = graphBuilder.addVertex(str);
                    try {
                        graphBuilder.addEdge(classVertex, vertex);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    public void setInternalInstances(String className, Map<String, InternalInstancesGraph> instances) {
    }

}
