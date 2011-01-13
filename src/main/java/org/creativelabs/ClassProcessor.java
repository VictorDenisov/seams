package org.creativelabs;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;

import org.creativelabs.report.*;

import java.util.*;

class ClassProcessor {
    private ClassOrInterfaceDeclaration typeDeclaration;

    protected Map<String, Set<Dependency>> dependencies = new HashMap<String, Set<Dependency>>();

    private DependencyCounterVisitorBuilder dependencyCounterBuilder;

    protected HashMap<String, InternalInstancesGraph> internalInstances
        = new HashMap<String, InternalInstancesGraph>();

    ClassProcessor(ClassOrInterfaceDeclaration typeDeclaration,
            DependencyCounterVisitorBuilder dependencyCounterBuilder) {
        this.typeDeclaration = typeDeclaration;
        this.dependencyCounterBuilder = dependencyCounterBuilder;
    }

    public void buildReport(ReportBuilder reportBuilder) {
        reportBuilder.setDependencies(typeDeclaration.getName(), dependencies);
        reportBuilder.setInternalInstances(typeDeclaration.getName(), internalInstances);
    }

    public DependenciesChart getDependenciesChart() {
        DependenciesChart chart = new DependenciesChart();
        for (Map.Entry<String, InternalInstancesGraph> entry : internalInstances.entrySet()) {
            chart.addInternalInstancesCountForMethod(entry.getKey(), entry.getValue().toSet().size());
        }
        Set<String> classDependencies = new HashSet<String>();
        for (Set<Dependency> dependencySet : dependencies.values()) {
            for (Dependency dependency : dependencySet) {
                classDependencies.add(dependency.getType() + "");
            }
        }
        chart.addDependenciesCountForClass(typeDeclaration.getName(), classDependencies.size());
        return chart;
    }

    public Map<String, InternalInstancesGraph> getInternalInstances() {
        return internalInstances;
    }

    public Map<String, Set<Dependency>> getDependencies() {
        return dependencies;
    }

    public void compute() {            
        for (BodyDeclaration bd : typeDeclaration.getMembers()) {
            if (bd instanceof MethodDeclaration) {
                MethodDeclaration md = (MethodDeclaration) bd;
                findOutgoingDependencies(md);
            }
        }
    }

    void findOutgoingDependencies(MethodDeclaration md) {
        BlockStmt body = md.getBody();
        dependencyCounterBuilder.setConstructedMethodArguments(md);
        DependencyCounterVisitor dependencyCounterVisitor = dependencyCounterBuilder.build();
        dependencyCounterVisitor.visit(body, null);

        if (dependencyCounterVisitor.getDependencies() != null) {
            dependencies.put(md.getName(), dependencyCounterVisitor.getDependencies());
        }
        if (dependencyCounterVisitor.getInternalInstances() != null) {
            internalInstances.put(md.getName(), dependencyCounterVisitor.getInternalInstances());
        }
    }

}

