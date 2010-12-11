package org.creativelabs;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;
import org.creativelabs.graph.InternalInstancesGraph;

import java.util.*;

class ClassProcessor {
    private ClassOrInterfaceDeclaration typeDeclaration;

    private VariableList fieldList;

    private ImportList imports;

    private Map<String, Set<Dependency>> dependencies = new HashMap<String, Set<Dependency>>();

    private InternalInstancesGraph internalInstancesGraph;

    public ClassProcessor(ClassOrInterfaceDeclaration typeDeclaration, ImportList imports, String className) {
        this.imports = imports;
        this.typeDeclaration = typeDeclaration;
        internalInstancesGraph = new InternalInstancesGraph(className);
        findFields();
    }

    public InternalInstancesGraph getInternalInstancesGraph() {
        return internalInstancesGraph;
    }

    public Map<String, Set<Dependency>> getDependencies() {
        return dependencies;
    }

    public void compute() {            
        processMethods(typeDeclaration);
    }

    private void processMethods(ClassOrInterfaceDeclaration n) {

        for (BodyDeclaration bd : n.getMembers()) {
            if (bd instanceof MethodDeclaration) {
                MethodDeclaration md = (MethodDeclaration) bd;

                findOutgoingDependencies(md);
            }
        }
    }

    private void findOutgoingDependencies(MethodDeclaration md) {
        BlockStmt body = md.getBody();
        DependencyCounterVisitor dependencyCounter = new DependencyCounterVisitor(fieldList, imports);
        dependencyCounter.visit(body, null);
        dependencies.put(md.getName(), dependencyCounter.getDependencies());
        internalInstancesGraph.addMethodInternalInstances(md.getName(), 
                dependencyCounter.getInternalInstances().toSet());
    }

    private void findFields() {
        fieldList = new VariableList(typeDeclaration, imports);
    }

}

