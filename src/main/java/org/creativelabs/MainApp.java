package org.creativelabs;

import java.io.*;
import java.util.*;

import japa.parser.ast.*;
import japa.parser.ast.visitor.*;
import japa.parser.ast.body.*;
import japa.parser.ast.type.*;
import japa.parser.*;

final class MainApp {

    private MainApp() {

    }

    public static void main(String... args) throws Exception {
        for (String path : args) {
            File file = new File(path);
            printToFile(file);
        }
    }

    private static void processClass(ClassOrInterfaceDeclaration typeDeclaration, String fileName) {
        ClassProcessor classProcessor = new ClassProcessor(typeDeclaration);
        classProcessor.compute();
        outData(classProcessor, fileName);
    }

    private static void outData(ClassProcessor classProcessor, String fileName) {
        try {
            File file = new File(fileName + ".deps");
            if (file.createNewFile()) {
                PrintWriter writer = new PrintWriter(file);
                printFields(classProcessor.getFields(), writer);
                printDeps("Dependencies", classProcessor.getDependencies(), writer);
                printDepsUponType("UponType", classProcessor.getDependencies(), writer);
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printFields(Map<String, String> fields, PrintWriter writer) {
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            writer.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    private static void printDeps(String depsName, Map<String, Set<Dependency>> deps, PrintWriter writer) {
        for (Map.Entry<String, Set<Dependency>> entry : deps.entrySet()) {
            writer.print(entry.getKey() + " -> ");
            outputSet(depsName, entry.getValue(), writer);
        }
    }

    private static void outputSet(String depsName, Set<Dependency> set, PrintWriter writer) {
        writer.println(depsName + "(");
        for (Dependency value : set) {
            writer.println(value.getExpression());
        }
        writer.println(")");
    }

    private static void printDepsUponType(String depsName, Map<String, Set<Dependency>> deps, PrintWriter writer) {
        for (Map.Entry<String, Set<Dependency>> entry : deps.entrySet()) {
            writer.print(entry.getKey() + " -> ");
            outputSetUponType(depsName, entry.getValue(), writer);
        }
    }

    private static void outputSetUponType(String depsName, Set<Dependency> set, PrintWriter writer) {
        writer.println(depsName + "(");
        for (Dependency value : set) {
            if (value.getType() != null) {
                writer.println(value.getType());
            }
        }
        writer.println(")");
    }

    private static void printToFile(File fileOrDirectory) throws Exception {
        if (!fileOrDirectory.exists()) {
            throw new IllegalArgumentException(fileOrDirectory.getAbsolutePath() + " doesn't exist");
        }
        if (fileOrDirectory.isDirectory()) {
            for (File directory : fileOrDirectory.listFiles()) {
                printToFile(directory);
            }
        } else {
            FileInputStream fis = new FileInputStream(fileOrDirectory);
            CompilationUnit cu = JavaParser.parse(fis);
            for (TypeDeclaration typeDeclaration : cu.getTypes()) {
                if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
                    processClass((ClassOrInterfaceDeclaration) typeDeclaration, fileOrDirectory.getAbsolutePath());
                }
            }
        }
    }
}

// vim: set ts=4 sw=4 et:
