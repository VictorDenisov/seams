package org.creativelabs.report;

import org.creativelabs.Dependency;
import org.creativelabs.iig.InternalInstancesGraph;

import java.util.Collection;
import java.util.Map;

public interface ReportBuilder {
    void setDependencies(String className, Map<String, Collection<Dependency>> dependencies);

    void setInternalInstances(String className, Map<String, InternalInstancesGraph> instances);
}
