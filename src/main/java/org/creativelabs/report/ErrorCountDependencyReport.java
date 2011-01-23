package org.creativelabs.report;

import java.util.*;
import org.creativelabs.*;

public class ErrorCountDependencyReport implements ReportBuilder {
    private int count = 0;

    private ArrayList<String> errorDependencies = new ArrayList<String>();
    
    public void setDependencies(String className, Map<String, Collection<Dependency>> dependencies) {
        for (Map.Entry<String, Collection<Dependency>> entry : dependencies.entrySet()) {
            for (Dependency dependency : entry.getValue()) {
                if (dependency.getType() == null || "ClassTypeError".equals(dependency.getType().getClass().getSimpleName())) {
                    errorDependencies.add(
                            "Error in : " + className + " : " + dependency.getExpression());
                    ++count;
                }
            }
        }
    }

    public void setInternalInstances(String className, Map<String, InternalInstancesGraph> instances) {
    }

    public int getCount() {
        return count;
    }

    public List<String> getErrorMessages() {
        return errorDependencies;
    }
}
