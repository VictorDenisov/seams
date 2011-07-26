package org.creativelabs;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.creativelabs.chart.BarChartBuilder;
import org.creativelabs.drawer.ChartDrawer;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.introspection.ReflectionAbstractionImpl;
import org.creativelabs.report.DataCollector;
import org.creativelabs.report.ReportBuilder;
import org.creativelabs.typefinder.DependenciesChart;
import org.creativelabs.typefinder.ImportList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public final class MainApp {

    private static Log log = LogFactory.getLog(MainApp.class);

    private static ImportList imports;

    private static String packageName;

    private static final int IMAGE_WIDTH = 2000;

    private static final int IMAGE_HEIGHT = 2000;

    private static final String USAGE = "[-h] [-d] [-g] [-c] -f <file or directory> [<file or directory> ...]";

    private static final String HEADER =
            "Seams.";
    private static final String FOOTER =
            "For more instructions, see our website at: https://github.com/VictorDenisov/seams";

    private static CommandLine commandLine = null;

    public static boolean NEED_TO_REWRITE_OLD_REPORT = false;

    private MainApp() {

    }

    public static void main(String... args) throws Exception {
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        options.addOption("h", "help", false, "Print this usage information");
        options.addOption("d", "dependency", false, "Create files with dependencies");
        options.addOption("g", "graph", false, "Create files with graphs");
        options.addOption("c", "chart", false, "Create file with chart");
        options.addOption("r", "rewrite", false, "Rewrite old reports");
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
        if (commandLine.hasOption('r')) {
            NEED_TO_REWRITE_OLD_REPORT = true;
        }
        if (commandLine.hasOption('f')) {
            DataCollector dataCollector = new DataCollector();

            for (String path : commandLine.getOptionValues('f')) {
                File file = new File(path);
                processFileOrDirectory(file, dataCollector);
            }
            dataCollector.buildDetailedDependencyReport();
            dataCollector.buildSsaFormRepresentationReport();
            dataCollector.buildSsaErrorsReport();
            dataCollector.buildNumberOfErrorsReport();
        }
    }

    private static void processClass(ClassOrInterfaceDeclaration typeDeclaration, String fileName, ReportBuilder reportBuilder) throws FileNotFoundException {
        log.info("Processing of class " + typeDeclaration.getName() + " in file " + fileName + "...");
        ClassProcessor classProcessor = new ClassProcessorBuilder()
                .setTypeDeclaration(typeDeclaration)
                .setPackage(packageName)
                .setImports(imports)
                .buildClassProcessor();
        classProcessor.compute();
        classProcessor.buildReport(reportBuilder);

        if (commandLine.hasOption('g')) {
            InternalInstancesGraph graph = classProcessor.getSsaInternalInstancesGraph();

////            Jung graph
//            JungGraphBuilder graphBuilder1 = new JungGraphBuilder();
//            graph.buildGraph(graphBuilder1);
//            new JungDrawer(graphBuilder1.getGraph()).saveToFile(IMAGE_WIDTH, IMAGE_HEIGHT,
//                    fileName);
//
//            PrintWriter writer = new PrintWriter(fileName + ".dot");
//            GraphvizGraphBuilder graphBuilder = new GraphvizGraphBuilder(writer);
//            graph.buildGraph(graphBuilder);
//            graphBuilder.finalizeGraph();
//            writer.flush();
//            writer.close();
//
//            new GraphvizDrawer().saveToFile(0, 0, fileName);
        }
        if (commandLine.hasOption('c')) {
            BarChartBuilder chartBuilder = new BarChartBuilder();
            DependenciesChart chart = classProcessor.getDependenciesChart();
            chart.buildChart(chartBuilder);
            new ChartDrawer(chartBuilder.getChart()).saveToFile(IMAGE_WIDTH, IMAGE_HEIGHT, fileName);
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
                packageName = cu.getPackage().getName().getName();
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
