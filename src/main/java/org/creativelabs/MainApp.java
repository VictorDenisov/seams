package org.creativelabs;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import org.apache.commons.cli.*;
import org.creativelabs.chart.BarChartBuilder;
import org.creativelabs.graph.JungGraphBuilder;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.introspection.ReflectionAbstractionImpl;
import org.creativelabs.report.DataCollector;
import org.creativelabs.report.ReportBuilder;
import org.creativelabs.ssa.SsaFormAstRepresentation;
import org.creativelabs.ui.ChartDrawer;
import org.creativelabs.ui.JungDrawer;
import org.creativelabs.ui.SsaDrawer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

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
        options.addOption("s", "ssa", false, "Create ssa form");
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
            DataCollector dataCollector = new DataCollector();

            for (String path : commandLine.getOptionValues('f')) {
                File file = new File(path);
                processFileOrDirectory(file, dataCollector);
            }
            dataCollector.buildDetailedDependencyReport();
            dataCollector.buildNumberOfErrorsReport();
        }
    }

    private static void processClass(ClassOrInterfaceDeclaration typeDeclaration, String fileName, ReportBuilder reportBuilder) throws FileNotFoundException {
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
        if (commandLine.hasOption('s')) {
            for (SsaFormAstRepresentation form : classProcessor.getForms()) {
                new SsaDrawer(form).saveToFile(IMAGE_WIDTH, IMAGE_HEIGHT,
                        fileName + "." + form.getMethodName());
            }
//            SimpleInternalInstancesGraph graph = classProcessor.getSsaInternalInstancesGraph();
//            JungGraphBuilder graphBuilder = new JungGraphBuilder();
//            graph.buildGraph(graphBuilder);
//            new JungDrawer(graphBuilder.getGraph()).saveToFile(2 * IMAGE_WIDTH, 2 * IMAGE_HEIGHT,
//                    fileName);

//            PrintWriter printWriter = new PrintWriter(classProcessor.);
//            GraphvizGraphBuilder graphBuilder = new GraphvizGraphBuilder(printWriter);
//            graph.buildGraph(graphBuilder);
//            new GraphvizDrawer().saveToFile(IMAGE_WIDTH, IMAGE_HEIGHT, fileName);
        }
    }

    private static void processFileOrDirectory(File fileOrDirectory, ReportBuilder reportBuilder) throws Exception {
        if (!fileOrDirectory.exists()) {
            throw new IllegalArgumentException(fileOrDirectory.getAbsolutePath() + " doesn't exist");
        }
        if (fileOrDirectory.isDirectory()) {
            for (File directory : fileOrDirectory.listFiles()) {
                processFileOrDirectory(directory, reportBuilder);
            }
        } else {
            String fileName = fileOrDirectory.getName();
            if (fileName.endsWith(".java")) {
                FileInputStream fis = new FileInputStream(fileOrDirectory);
                CompilationUnit cu = JavaParser.parse(fis);
                for (TypeDeclaration typeDeclaration : cu.getTypes()) {
                    if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
                        imports =
                                new ImportList(ReflectionAbstractionImpl.create(), cu,
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
