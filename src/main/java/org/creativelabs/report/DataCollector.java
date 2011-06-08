package org.creativelabs.report;

import org.creativelabs.typefinder.Dependency;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.ssa.representation.SsaFormRepresentation;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

public class DataCollector implements ReportBuilder {

    Map<String, ClassData> map = new HashMap<String, ClassData>();

    private class ClassData {
        private Map<String, Collection<Dependency>> dependencies;
        private Map<String, InternalInstancesGraph> internalInstances;
        private Set<SsaFormRepresentation> ssaFormRepresentations;
    }
    
    public void setDependencies(String className, Map<String, Collection<Dependency>> dependencies) {
        ClassData cd;
        if (map.containsKey(className)) {
            cd = map.get(className);
        } else {
            cd = new ClassData();
            map.put(className, cd);
        }
        cd.dependencies = dependencies;
    }

    public void setInternalInstances(String className, Map<String, InternalInstancesGraph> instances) {
        ClassData cd;
        if (map.containsKey(className)) {
            cd = map.get(className);
        } else {
            cd = new ClassData();
            map.put(className, cd);
        }
        cd.internalInstances = instances;
    }

    @Override
    public void setSsaFormRepresentations(String className, Set<SsaFormRepresentation> ssaFormRepresentations) {
        ClassData cd;
        if (map.containsKey(className)) {
            cd = map.get(className);
        } else {
            cd = new ClassData();
            map.put(className, cd);
        }
        cd.ssaFormRepresentations = ssaFormRepresentations;
    }

    public void buildReport(ReportBuilder reportBuilder) {
        for (Map.Entry<String, ClassData> entry : map.entrySet()) {
            reportBuilder.setDependencies(entry.getKey(), entry.getValue().dependencies);
            reportBuilder.setInternalInstances(entry.getKey(), entry.getValue().internalInstances);
            reportBuilder.setSsaFormRepresentations(entry.getKey(), entry.getValue().ssaFormRepresentations);
        }
    }

    public void buildDetailedDependencyReport() {
        buildReport(new DetailedDependencyReportBuilder());
    }

    public void buildSsaFormRepresentationReport() {
        buildReport(new SsaFormRepresentationsReportBuilder());
    }

    public void buildNumberOfErrorsReport() throws Exception {
        ErrorCountDependencyReport report = new ErrorCountDependencyReport();
        buildReport(report);
        int numberOfErrors = report.getCount();
        List<String> errorDeps = report.getErrorMessages();

        PrintWriter pw = new PrintWriter(new FileWriter("errors.txt"));
        pw.println("Number of errors : " + numberOfErrors);
        for (String dependency : errorDeps) {
            pw.println(dependency);
        }
        pw.close();
    }
}
