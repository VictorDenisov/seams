package org.creativelabs.report;

import java.util.*;
import org.creativelabs.*;
import java.io.*;

public class DataCollector implements ReportBuilder {

    Map<String, ClassData> map = new HashMap<String, ClassData>();

    private class ClassData {
        private Map<String, Collection<Dependency>> dependencies;
        private Map<String, InternalInstancesGraph> internalInstances;
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

    public void buildReport(ReportBuilder reportBuilder) {
        for (Map.Entry<String, ClassData> entry : map.entrySet()) {
            reportBuilder.setDependencies(entry.getKey(), entry.getValue().dependencies);
            reportBuilder.setInternalInstances(entry.getKey(), entry.getValue().internalInstances);
        }
    }

    public void buildDetailedDependencyReport() {
        buildReport(new DetailedDependencyReportBuilder());
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

    public void buildInternalInstancesByClassReport() throws Exception {
        InternalInstancesByClassReportBuilder report = new InternalInstancesByClassReportBuilder();
        buildReport(report);
        report.saveToFile("internal_instances_by_class.jpg");
    }
}
