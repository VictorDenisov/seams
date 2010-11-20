package org.creativelabs;

import java.io.*;
import java.util.*;
import japa.parser.ast.*;
import japa.parser.ast.visitor.*;
import japa.parser.ast.body.*;
import japa.parser.ast.type.*;
import japa.parser.*;

class MainApp {

    public static void main(String[] args) throws Exception {
        FileInputStream fis = new FileInputStream(new File("Sample.java"));

        CompilationUnit cu = JavaParser.parse(fis);
        for (TypeDeclaration typeDeclaration : cu.getTypes()) {
            if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
                processClass((ClassOrInterfaceDeclaration) typeDeclaration);
            }
        }
    }

    private static void processClass(ClassOrInterfaceDeclaration typeDeclaration) {
        ClassProcessor classProcessor = new ClassProcessor(typeDeclaration);
        classProcessor.compute();
        outData(classProcessor);
    }

    private static void outData(ClassProcessor classProcessor) {
        printFields(classProcessor.getFields());
        printDeps("Dependencies", classProcessor.getDependencies());
        printDeps("UponType", classProcessor.getDependenciesUponType());
    }

    private static void printFields(Map<String, String> fields) {
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    private static void printDeps(String depsName, Map<String, Set<String>> deps) {
        for (Map.Entry<String, Set<String>> entry : deps.entrySet()) {
            System.out.print(entry.getKey() + " -> ");
            outputSet(depsName, entry.getValue());
        }
    }

    private static void outputSet(String depsName, Set<String> set) {
        System.out.println(depsName + "(");
        for (String value : set) {
            System.out.println(value);
        }
        System.out.println(")");
    }
}

// vim: set ts=4 sw=4 et:
