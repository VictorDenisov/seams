package org.creativelabs.chart;

import java.util.HashMap;
import java.util.Map;

public class ToStringChartBuilder implements ChartBuilder {

    private Map<String, Integer> dependencies = new HashMap<String, Integer>();
    private Map<String, Integer> internalInstances = new HashMap<String, Integer>();

    @Override
    public void setDependencyCountForClass(String className, int countOfDependencies) {
        dependencies.put(className, countOfDependencies);

    }

    @Override
    public void setInternalInstancesCountForMethod(String methodName, int countOfInternalInstances) {
        internalInstances.put(methodName, countOfInternalInstances);
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer("{");
        for (Map.Entry<String, Integer> entry : dependencies.entrySet()) {
            result.append(entry.getKey() + " -> "
                    + entry.getValue() + ", ");
        }
        result.append("} {");
        for (Map.Entry<String, Integer> entry : internalInstances.entrySet()) {
            result.append(entry.getKey() + " -> "
                    + entry.getValue() + ", ");
        }
        return result.toString() + "}";
    }
}
