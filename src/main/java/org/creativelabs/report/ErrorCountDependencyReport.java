package org.creativelabs.report;

import org.creativelabs.Dependency;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.introspection.ClassTypeError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ErrorCountDependencyReport implements ReportBuilder {
    private int count = 0;

    private ArrayList<String> errorDependencies = new ArrayList<String>();
    
    public void setDependencies(String className, Map<String, Collection<Dependency>> dependencies) {
        for (Map.Entry<String, Collection<Dependency>> entry : dependencies.entrySet()) {
            for (Dependency dependency : entry.getValue()) {
                if (dependency.getType() == null || dependency.getType() instanceof ClassTypeError) {
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
