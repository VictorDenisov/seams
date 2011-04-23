package org.creativelabs.report;

import org.creativelabs.Dependency;
import org.creativelabs.graph.ToStringGraphBuilder;
import org.creativelabs.introspection.ClassTypeStub;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import static org.creativelabs.graph.condition.VertexConditions.EMPTY_CONDITIONS_STRING;
import static org.testng.AssertJUnit.assertEquals;

public class DependencyGraphReportBuilderTest {

    @Test
    public void testSetDependencies() {
        ToStringGraphBuilder graphBuilder = new ToStringGraphBuilder();
        DependencyGraphReportBuilder reportBuilder = new DependencyGraphReportBuilder(graphBuilder);

        Map<String, Collection<Dependency>> map = new TreeMap<String, Collection<Dependency>>();

        Collection<Dependency> deps = new ArrayList<Dependency>();
        deps.add(new Dependency(null, new ClassTypeStub("String")));
        deps.add(new Dependency(null, new ClassTypeStub("Map")));
        map.put("methodA", deps);
        reportBuilder.setDependencies("ClassA", map);

        map = new TreeMap<String, Collection<Dependency>>();
        deps = new ArrayList<Dependency>();
        deps.add(new Dependency(null, new ClassTypeStub("ClassA")));
        deps.add(new Dependency(null, new ClassTypeStub("Map")));
        map.put("methodC", deps);
        reportBuilder.setDependencies("ClassB", map);

        assertEquals("{ClassA" + EMPTY_CONDITIONS_STRING + " -> Map" + EMPTY_CONDITIONS_STRING +
                ", ClassA" + EMPTY_CONDITIONS_STRING + " -> String" + EMPTY_CONDITIONS_STRING +
                ", ClassB" + EMPTY_CONDITIONS_STRING + " -> ClassA" + EMPTY_CONDITIONS_STRING +
                ", ClassB" + EMPTY_CONDITIONS_STRING + " -> Map" + EMPTY_CONDITIONS_STRING + ", }",
                graphBuilder.toString());
    }

    @Test
    public void testSetDependenciesMultiple() {
        ToStringGraphBuilder graphBuilder = new ToStringGraphBuilder();
        DependencyGraphReportBuilder reportBuilder = new DependencyGraphReportBuilder(graphBuilder);

        Map<String, Collection<Dependency>> map = new TreeMap<String, Collection<Dependency>>();

        Collection<Dependency> deps = new ArrayList<Dependency>();
        deps.add(new Dependency(null, new ClassTypeStub("String")));
        deps.add(new Dependency(null, new ClassTypeStub("Map")));
        map.put("methodA", deps);
        deps = new ArrayList<Dependency>();
        deps.add(new Dependency(null, new ClassTypeStub("String")));
        deps.add(new Dependency(null, new ClassTypeStub("Map")));
        map.put("methodB", deps);
        reportBuilder.setDependencies("ClassA", map);

        map = new TreeMap<String, Collection<Dependency>>();
        deps = new ArrayList<Dependency>();
        deps.add(new Dependency(null, new ClassTypeStub("ClassA")));
        deps.add(new Dependency(null, new ClassTypeStub("Map")));
        map.put("methodC", deps);
        reportBuilder.setDependencies("ClassB", map);

        assertEquals("{ClassA" + EMPTY_CONDITIONS_STRING + " -> Map" + EMPTY_CONDITIONS_STRING +
                ", ClassA" + EMPTY_CONDITIONS_STRING + " -> String" + EMPTY_CONDITIONS_STRING +
                ", ClassB" + EMPTY_CONDITIONS_STRING + " -> ClassA" + EMPTY_CONDITIONS_STRING +
                ", ClassB" + EMPTY_CONDITIONS_STRING + " -> Map" + EMPTY_CONDITIONS_STRING + ", }",
                graphBuilder.toString());
    }
}
