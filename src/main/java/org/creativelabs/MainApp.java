package org.creativelabs;

import java.io.*;
import java.util.*;
import japa.parser.ast.*;
import japa.parser.ast.visitor.*;
import japa.parser.ast.body.*;
import japa.parser.ast.type.*;
import japa.parser.*;

final class MainApp {

    private static ImportList imports;

    private MainApp() {

    }

    public static void main(String[] args) throws Exception {
        FileInputStream fis = new FileInputStream(new File("Sample.java"));

        CompilationUnit cu = JavaParser.parse(fis);
        imports = new ImportList(cu);
        for (TypeDeclaration typeDeclaration : cu.getTypes()) {
            if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
                processClass((ClassOrInterfaceDeclaration) typeDeclaration);
            }
        }
    }

    private static void processClass(ClassOrInterfaceDeclaration typeDeclaration) {
        ClassProcessor classProcessor = new ClassProcessor(typeDeclaration, imports);
        classProcessor.compute();
        outData(classProcessor);
    }

    private static void outData(ClassProcessor classProcessor) {
        printDeps("Dependencies", classProcessor.getDependencies());
        printDepsUponType("UponType", classProcessor.getDependencies());
    }

    private static void printFields(Map<String, String> fields) {
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    private static void printDeps(String depsName, Map<String, Set<Dependency>> deps) {
        for (Map.Entry<String, Set<Dependency>> entry : deps.entrySet()) {
            System.out.print(entry.getKey() + " -> ");
            outputSet(depsName, entry.getValue());
        }
    }

    private static void outputSet(String depsName, Set<Dependency> set) {
        System.out.println(depsName + "(");
        for (Dependency value : set) {
            System.out.println(value.getExpression());
        }
        System.out.println(")");
    }

    private static void printDepsUponType(String depsName, Map<String, Set<Dependency>> deps) {
        for (Map.Entry<String, Set<Dependency>> entry : deps.entrySet()) {
            System.out.print(entry.getKey() + " -> ");
            outputSetUponType(depsName, entry.getValue());
        }
    }

    private static void outputSetUponType(String depsName, Set<Dependency> set) {
        System.out.println(depsName + "(");
        for (Dependency value : set) {
            if (value.getType() != null) {
                System.out.println(value.getType());
            }
        }
        System.out.println(")");
    }
}

// vim: set ts=4 sw=4 et:
