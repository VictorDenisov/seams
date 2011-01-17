package org.creativelabs.report;

import java.util.*;
import org.creativelabs.*;

public class DataCollector implements ReportBuilder {

    Map<String, ClassData> map = new HashMap<String, ClassData>();

    private class ClassData {
        private Map<String, Collection<Dependency>> dependencies;
    }
    
    public void setDependencies(String className, Map<String, Collection<Dependency>> dependencies) {
        ClassData cd = new ClassData();
        cd.dependencies = dependencies;
        map.put(className, cd);
    }

    public void setInternalInstances(String className, Map<String, InternalInstancesGraph> instances) {
    }

    public void buildReport(ReportBuilder reportBuilder) {
        for (Map.Entry<String, ClassData> entry : map.entrySet()) {
            reportBuilder.setDependencies(entry.getKey(), entry.getValue().dependencies);
        }
    }
}
