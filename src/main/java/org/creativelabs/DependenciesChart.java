package org.creativelabs;


import org.creativelabs.chart.ChartBuilder;

import java.util.HashMap;
import java.util.Map;

public class DependenciesChart {

    private Map<String, Integer> dependenciesCountOfClass = new HashMap<String, Integer>();
    private Map<String, Integer> internalInstancesCountOfMethod = new HashMap<String, Integer>();

    public void addDependenciesCountForClass(String className, int count) {
        dependenciesCountOfClass.put(className, count);
    }

    public void addInternalInstancesCountForMethod(String methodName, int count) {
        internalInstancesCountOfMethod.put(methodName, count);
    }

    public void buildChart(ChartBuilder builder) {
        for (Map.Entry<String, Integer> entry : dependenciesCountOfClass.entrySet()) {
            builder.setDependencyCountForClass(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Integer> entry : internalInstancesCountOfMethod.entrySet()) {
            builder.setInternalInstancesCountForMethod(entry.getKey(), entry.getValue());
        }
    }

}
