package org.creativelabs;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;

import java.util.*;

class ClassProcessor {
    private ClassOrInterfaceDeclaration typeDeclaration;

    private VariableList fieldList;

    private ImportList imports;

    private String className;

    private Map<String, Set<Dependency>> dependencies = new HashMap<String, Set<Dependency>>();

    private HashMap<String, NewInternalInstancesGraph> internalInstances
            = new HashMap<String, NewInternalInstancesGraph>();

    public ClassProcessor(ClassOrInterfaceDeclaration typeDeclaration, ImportList imports, String className) {
        this.imports = imports;
        this.typeDeclaration = typeDeclaration;
        this.fieldList = new VariableList(typeDeclaration, imports);
    }

    public Map<String, NewInternalInstancesGraph> getInternalInstances() {
        return internalInstances;
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

    void findOutgoingDependencies(MethodDeclaration md) {
        BlockStmt body = md.getBody();
        DependencyCounterVisitor dependencyCounter = new DependencyCounterVisitor(fieldList, imports, className);
        dependencyCounter.visit(body, null);
        if (dependencyCounter.getDependencies() != null) {
            dependencies.put(md.getName(), dependencyCounter.getDependencies());
        }
        if (dependencyCounter.getInternalInstances() != null) {
            internalInstances.put(md.getName(), dependencyCounter.getInternalInstances());
        }
    }

}

