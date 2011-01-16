package org.creativelabs.report;

import java.util.*;
import org.creativelabs.*;

public interface ReportBuilder {
    void setDependencies(String className, Map<String, Collection<Dependency>> dependencies);

    void setInternalInstances(String className, Map<String, InternalInstancesGraph> instances);
}
