package org.creativelabs.report;

import org.creativelabs.*;
import org.creativelabs.graph.*;
import org.creativelabs.introspection.*;
import org.testng.annotations.Test;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;

import java.util.*;

import static org.testng.AssertJUnit.*;
import static org.mockito.Mockito.*;

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

        assertEquals("{ClassA -> Map, ClassA -> String, ClassB -> ClassA, ClassB -> Map, }",
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

        assertEquals("{ClassA -> Map, ClassA -> String, ClassB -> ClassA, ClassB -> Map, }",
                graphBuilder.toString());
    }
}
