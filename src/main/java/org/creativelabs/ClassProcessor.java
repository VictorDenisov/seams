package org.creativelabs;

import java.io.*;
import japa.parser.ast.*;
import japa.parser.ast.visitor.*;
import japa.parser.ast.body.*;
import japa.parser.ast.type.*;
import japa.parser.ast.stmt.*;
import japa.parser.*;
import java.util.*;

class ClassProcessor {
    private ClassOrInterfaceDeclaration typeDeclaration;

    private VariableList fieldList;

    private ImportList imports;

    private Map<String, Set<Dependency>> dependencies = new HashMap<String, Set<Dependency>>();

    public ClassProcessor(ClassOrInterfaceDeclaration typeDeclaration, ImportList imports) {
        this.imports = imports;
        this.typeDeclaration = typeDeclaration;
        findFields();
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
    }

    private void findFields() {
        fieldList = new VariableList(typeDeclaration, imports);
    }

}

