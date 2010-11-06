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

    public ClassProcessor(ClassOrInterfaceDeclaration typeDeclaration) {
        this.typeDeclaration = typeDeclaration;
        this.fields = findFields(typeDeclaration);
    }
        
    private Map<String, String> fields;

    private Map<String, Set<String>> dependencies = new HashMap<String, Set<String>>();
    private Map<String, Set<String>> dependenciesUponType = new HashMap<String, Set<String>>();

    public Map<String, String> getFields() {
        return fields;
    }

    public Map<String, Set<String>> getDependencies() {
        return dependencies;
    }

    public Map<String, Set<String>> getDependenciesUponType() {
        return dependenciesUponType;
    }

    public void compute() {            
        processMethods(typeDeclaration, fields);
    }

    private void processMethods(ClassOrInterfaceDeclaration n, 
                    Map<String, String> classFields) {

        for (BodyDeclaration bd : n.getMembers()) {
            if (bd instanceof MethodDeclaration) {
                MethodDeclaration md = (MethodDeclaration) bd;
                Map<String, Set<String>> res = findOutgoingDependencies(md, classFields);
                dependencies.put(md.getName(), res.get("deps"));
                dependenciesUponType.put(md.getName(), res.get("depsUponType"));
            }
        }
    }

    private Map<String, Set<String>>  findOutgoingDependencies(MethodDeclaration md, 
                    Map<String, String> classFields) {
        BlockStmt body = md.getBody();
        DependencyCounterVisitor dependencyCounter = new DependencyCounterVisitor(classFields);
        dependencyCounter.visit(body, null);

        Map<String, Set<String>> result = new HashMap<String, Set<String>>();
        result.put("deps", dependencyCounter.getDependencies());
        result.put("depsUponType", dependencyCounter.getDependenciesUponType());
        return result;
    }

    private Map<String, String> findFields(ClassOrInterfaceDeclaration typeDeclaration) {

        Map<String, String> fields = new HashMap<String, String>();
        for (BodyDeclaration bd : typeDeclaration.getMembers()) {
            if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = (FieldDeclaration) bd;
                for (VariableDeclarator vardecl : fd.getVariables()) {
                    TypeVisitor tv = new TypeVisitor();
                    fd.getType().accept(tv, null);
                    fields.put(vardecl.getId().getName(), tv.getName());
                }
            }
        }
        return fields;
    }

    private class TypeVisitor extends VoidVisitorAdapter<Object> {
        private String name;

        String getName() {
            return name;
        }

        @Override
        public void visit(ClassOrInterfaceType n, Object a) { 
            name = n.getName();
        }

        @Override
        public void visit(PrimitiveType n, Object a) {
            name = n.getType().toString();
        }
    }

}
