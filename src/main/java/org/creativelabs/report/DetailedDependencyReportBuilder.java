package org.creativelabs.report;

import org.creativelabs.Dependency;
import org.creativelabs.iig.InternalInstancesGraph;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

public class DetailedDependencyReportBuilder implements ReportBuilder {

    public void setDependencies(String className, Map<String, Collection<Dependency>> dependencies) {
        outData(dependencies, "detailedreport/" + className);
    }

    public void setInternalInstances(String className, Map<String, InternalInstancesGraph> instances) {
    }

    private void outData(Map<String, Collection<Dependency>> deps, String fileName) {
        try {
            File file = new File(fileName + ".deps");
            if (file.createNewFile()) {
                PrintWriter writer = new PrintWriter(file);
                printDeps(deps, writer);
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printDeps(Map<String, Collection<Dependency>> deps, PrintWriter writer) {
        for (Map.Entry<String, Collection<Dependency>> entry : deps.entrySet()) {
            writer.print(entry.getKey() + " -> ");
            outputSet(entry.getValue(), writer);
        }
    }

    private void outputSet(Collection<Dependency> set, PrintWriter writer) {
        writer.println("Dependencies (");
        for (Dependency value : set) {
            writer.println("    " + value.getExpression() + " -- " + value.getType());
        }
        writer.println(")");
    }
}
