package org.creativelabs;

import java.io.*;
import java.util.*;

import japa.parser.ast.*;
import japa.parser.ast.body.*;
import japa.parser.*;

import org.apache.commons.cli.*;
import org.creativelabs.chart.BarChartBuilder;
import org.creativelabs.ui.*;
import org.creativelabs.report.*;
import org.creativelabs.graph.JungGraphBuilder;
import org.creativelabs.introspection.ReflectionAbstractionImpl;

final class MainApp {

    private static ImportList imports;

    private static final int IMAGE_WIDTH = 300;

    private static final int IMAGE_HEIGHT = 300;

    private static final String USAGE = "[-h] [-d] [-g] [-c] -f <file or directory> [<file or directory> ...]";

    private static final String HEADER =
            "Seams.";
    private static final String FOOTER =
            "For more instructions, see our website at: https://github.com/VictorDenisov/seams";

    private static CommandLine commandLine = null;

    private MainApp() {

    }

    public static void main(String... args) throws Exception {
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        options.addOption("h", "help", false, "Print this usage information");
        options.addOption("d", "dependency", false, "Create files with dependencies");
        options.addOption("g", "graph", false, "Create files with graphs");
        options.addOption("c", "chart", false, "Create file with chart");
        options.addOptionGroup(new OptionGroup());
        Option option = OptionBuilder.
                isRequired(true).
                hasArgs().
                withArgName("file or directory").
                withDescription("Select files or directories for processing").
                withLongOpt("files").
                create('f');
        options.addOption(option);
        commandLine = parser.parse(options, args);
        if (commandLine.hasOption('h')
                || commandLine.getOptions().length == 0) {
            printUsage(options);
            System.exit(0);
        }
        if (commandLine.hasOption('f')) {
            OverviewReportBuilder reportBuilder = new OverviewReportBuilder();
            for (String path : commandLine.getOptionValues('f')) {
                File file = new File(path);
                printToFile(file, reportBuilder);
            }
            reportBuilder.saveToFile("overview.txt");
        }
    }

    private static void processClass(ClassOrInterfaceDeclaration typeDeclaration, String fileName, OverviewReportBuilder reportBuilder) {
        ClassProcessor classProcessor = new ClassProcessorBuilder()
                .setTypeDeclaration(typeDeclaration)
                .setImports(imports)
                .buildClassProcessor();
        classProcessor.compute();
        classProcessor.buildReport(reportBuilder);

        if (commandLine.hasOption('g')) {
            for (Map.Entry<String, InternalInstancesGraph> entry
                    : classProcessor.getInternalInstances().entrySet()) {
                InternalInstancesGraph graph = entry.getValue();
                String methodName = entry.getKey();
                JungGraphBuilder graphBuilder = new JungGraphBuilder();
                graph.buildGraph(graphBuilder);
                new JungDrawer(graphBuilder.getGraph()).saveToFile(IMAGE_WIDTH, IMAGE_HEIGHT,
                        fileName + "." + methodName);
            }
        }
        if (commandLine.hasOption('c')) {
            BarChartBuilder chartBuilder = new BarChartBuilder();
            DependenciesChart chart = classProcessor.getDependenciesChart();
            chart.buildChart(chartBuilder);
            new ChartDrawer(chartBuilder.getChart()).saveToFile(IMAGE_WIDTH, IMAGE_HEIGHT, fileName);
        }
        if (commandLine.hasOption('d')) {
            outData(classProcessor, fileName);
        }
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
            writer.println("    " + value.getExpression() + " -- " + value.getType());
        }
        writer.println(")");
    }

    private static void printToFile(File fileOrDirectory, OverviewReportBuilder reportBuilder) throws Exception {
        if (!fileOrDirectory.exists()) {
            throw new IllegalArgumentException(fileOrDirectory.getAbsolutePath() + " doesn't exist");
        }
        if (fileOrDirectory.isDirectory()) {
            for (File directory : fileOrDirectory.listFiles()) {
                printToFile(directory, reportBuilder);
            }
        } else {
            String fileName = fileOrDirectory.getName();
            if (fileName.endsWith(".java")) {
                FileInputStream fis = new FileInputStream(fileOrDirectory);
                CompilationUnit cu = JavaParser.parse(fis);
                for (TypeDeclaration typeDeclaration : cu.getTypes()) {
                    if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
                        imports = 
                            new ImportList(new ReflectionAbstractionImpl(), cu, 
                                    (ClassOrInterfaceDeclaration) typeDeclaration);

                        processClass((ClassOrInterfaceDeclaration) typeDeclaration,
                                fileOrDirectory.getAbsolutePath(), reportBuilder);
                    }
                }
            }
        }
    }

    private static void printUsage(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth(80);
        helpFormatter.printHelp(USAGE, HEADER, options, FOOTER);
    }

}

// vim: set ts=4 sw=4 et:
