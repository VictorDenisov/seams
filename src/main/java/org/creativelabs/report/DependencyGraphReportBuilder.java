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

    public void setDependencies(String className, Map<String, Collection<Dependency>> dependencies) {
        Vertex classVertex = map.get(className);

        if (classVertex == null) {
            classVertex = graphBuilder.addVertex(className);
        }

        Set<String> deps = new TreeSet<String>();
        for (Map.Entry<String, Collection<Dependency>> entry : dependencies.entrySet()) {
            for (Dependency dependency : entry.getValue()) {
                deps.add(dependency.getType().getShortString());
            }
        }
        for (String str : deps) {
            Vertex vertex;
            if (map.containsKey(str)) {
                vertex = map.get(str);
            } else {
                vertex = graphBuilder.addVertex(str);
            }
            try {
                graphBuilder.addEdge(classVertex, vertex);
            } catch (Exception e) {
            }
        }
    }

    public void setInternalInstances(String className, Map<String, InternalInstancesGraph> instances) {
    }

}
