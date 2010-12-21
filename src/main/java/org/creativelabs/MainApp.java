package org.creativelabs;

import java.io.*;
import java.util.*;

import japa.parser.ast.*;
import japa.parser.ast.body.*;
import japa.parser.*;

import japa.parser.ast.type.ClassOrInterfaceType;
import org.creativelabs.ui.JungDrawer;
import org.creativelabs.graph.JungGraphBuilder;
import javax.swing.*;

final class MainApp {

    private static ImportList imports;

    private static final int IMAGE_WIDTH = 300;

    private static final int IMAGE_HEIGHT = 300;

    private MainApp() {

    }

    public static void main(String... args) throws Exception {
        for (String path : args) {
            File file = new File(path);
            printToFile(file);
        }
    }

    private static void processClass(ClassOrInterfaceDeclaration typeDeclaration, String fileName) {
        ClassProcessor classProcessor = new ClassProcessorBuilder()
            .setTypeDeclaration(typeDeclaration)
            .setImportList(imports)
            .buildClassProcessor();
        classProcessor.compute();

        for (Map.Entry<String, InternalInstancesGraph> entry
                : classProcessor.getInternalInstances().entrySet()) {
            InternalInstancesGraph graph = entry.getValue();
            String methodName = entry.getKey();
            JFrame frame = new JFrame(fileName + " -- " + methodName);
            JungGraphBuilder builder = new JungGraphBuilder();
            graph.buildGraph(builder);
            new JungDrawer(builder.getGraph()).saveToFile(frame, IMAGE_WIDTH, IMAGE_HEIGHT, 
                    fileName + "." + methodName);
        }
        outData(classProcessor, fileName);
    }

    private static void outData(ClassProcessor classProcessor, String fileName) {
        try {
            File file = new File(fileName + ".deps");
            if (file.createNewFile()) {
                PrintWriter writer = new PrintWriter(file);
                printDeps(classProcessor.getDependencies(), writer);
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printDeps(Map<String, Set<Dependency>> deps, PrintWriter writer) {
        for (Map.Entry<String, Set<Dependency>> entry : deps.entrySet()) {
            writer.print(entry.getKey() + " -> ");
            outputSet(entry.getValue(), writer);
        }
    }

    private static void outputSet(Set<Dependency> set, PrintWriter writer) {
        writer.println("Dependencies (");
        for (Dependency value : set) {
            writer.println(value.getExpression() + " -- " + value.getType());
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
            String fileName = fileOrDirectory.getName();
            if (fileName.endsWith(".java")) {
                FileInputStream fis = new FileInputStream(fileOrDirectory);
                CompilationUnit cu = JavaParser.parse(fis);
                imports = new ImportList(cu);
                for (TypeDeclaration typeDeclaration : cu.getTypes()) {
                    if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
                        processClass((ClassOrInterfaceDeclaration) typeDeclaration,
                                fileOrDirectory.getAbsolutePath());
                    }
                }
            }
        }
    }
}

// vim: set ts=4 sw=4 et:
