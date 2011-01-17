package org.creativelabs.report;

import java.util.*;
import org.creativelabs.*;

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
}
