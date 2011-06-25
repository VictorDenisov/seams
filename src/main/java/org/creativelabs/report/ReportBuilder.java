package org.creativelabs.report;

import org.creativelabs.ssa.SsaError;
import org.creativelabs.typefinder.Dependency;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.ssa.representation.SsaFormRepresentation;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ReportBuilder {
    void setDependencies(String className, Map<String, Collection<Dependency>> dependencies);

    void setInternalInstances(String className, Map<String, InternalInstancesGraph> instances);

    void setSsaFormRepresentations(String className, Set<SsaFormRepresentation> ssaFormRepresentations);

    void setSsaErrors(String className, Set<SsaError> ssaErrors);
}
