package org.creativelabs.report;

import org.creativelabs.ssa.SsaError;
import org.creativelabs.typefinder.Dependency;
import org.creativelabs.graph.GraphBuilder;
import org.creativelabs.graph.Vertex;
import org.creativelabs.graph.condition.EmptyCondition;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.ssa.representation.SsaFormRepresentation;

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
            classVertex = graphBuilder.addVertex(className, new EmptyCondition(), new EmptyCondition());
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
                vertex = graphBuilder.addVertex(str, new EmptyCondition(), new EmptyCondition());
            }
            try {
                graphBuilder.addEdge(classVertex, vertex);
            } catch (Exception e) {
            }
        }
    }

    public void setInternalInstances(String className, Map<String, InternalInstancesGraph> instances) {

    }

    @Override
    public void setSsaFormRepresentations(String className, Set<SsaFormRepresentation> ssaFormRepresentations) {
    }

    @Override
    public void setSsaErrors(String className, Set<SsaError> ssaErrors) {
    }

}
