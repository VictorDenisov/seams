package org.creativelabs.report;

import org.creativelabs.ssa.SsaError;
import org.creativelabs.typefinder.Dependency;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.ssa.representation.SsaFormRepresentation;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OverviewReportBuilder implements ReportBuilder {

    protected Map<String, Map<String, Integer>> dependencyCount = new HashMap<String, Map<String, Integer>>();

    protected Map<String, Map<String, Integer>> internalInstancesCount = new HashMap<String, Map<String, Integer>>();

    public void setDependencies(String className, Map<String, Collection<Dependency>> dependencies) {
        Map resultMap = new HashMap<String, Integer>();
        for (Map.Entry<String, Collection<Dependency>> entry : dependencies.entrySet()) {
            resultMap.put(entry.getKey(), entry.getValue().size());
        }
        dependencyCount.put(className, resultMap);
    }

    public void setInternalInstances(String className, Map<String, InternalInstancesGraph> instances) {
        Map resultMap = new HashMap<String, Integer>();
        for (Map.Entry<String, InternalInstancesGraph> entry : instances.entrySet()) {
            resultMap.put(entry.getKey(), entry.getValue().toSet().size());
        }
        internalInstancesCount.put(className, resultMap);
    }

    @Override
    public void setSsaFormRepresentations(String className, Set<SsaFormRepresentation> ssaFormRepresentations) {
    }

    @Override
    public void setSsaErrors(String className, Set<SsaError> ssaErrors) {
    }

    public void saveToFile(PrintWriter writer) throws Exception {
        if (dependencyCount.keySet().size() != internalInstancesCount.keySet().size()) {
            throw new IllegalStateException();
        }

        for (String className : dependencyCount.keySet()) {
            writer.println("class " + className);
            Map<String, Integer> depCountByMethod = dependencyCount.get(className);
            Map<String, Integer> intInstanceByMethod = internalInstancesCount.get(className);
            if (depCountByMethod.keySet().size() != intInstanceByMethod.keySet().size()) {
                throw new IllegalStateException();
            }
            for (String methodName : depCountByMethod.keySet()) {
                writer.println("    method " + methodName);
                writer.println("        dependencies " + depCountByMethod.get(methodName));
                writer.println("        internal instances " + intInstanceByMethod.get(methodName));
            }
        }

        writer.close();
    }
}
