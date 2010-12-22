package org.creativelabs;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;

import java.util.*;

class ClassProcessor {
    private ClassOrInterfaceDeclaration typeDeclaration;

    private Map<String, Set<Dependency>> dependencies = new HashMap<String, Set<Dependency>>();

    private DependencyCounterVisitor dependencyCounter;

    private HashMap<String, InternalInstancesGraph> internalInstances
        = new HashMap<String, InternalInstancesGraph>();

    ClassProcessor(ClassOrInterfaceDeclaration typeDeclaration,
            DependencyCounterVisitor dependencyCounter) {
        this.typeDeclaration = typeDeclaration;
        this.dependencyCounter = dependencyCounter;
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
        dependencyCounter.cleanUp();
        dependencyCounter.visit(body, null);
        if (dependencyCounter.getDependencies() != null) {
            dependencies.put(md.getName(), dependencyCounter.getDependencies());
        }
        if (dependencyCounter.getInternalInstances() != null) {
            internalInstances.put(md.getName(), dependencyCounter.getInternalInstances());
        }
    }

}

