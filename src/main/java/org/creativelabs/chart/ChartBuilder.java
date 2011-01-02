package org.creativelabs.chart;

public interface ChartBuilder {

    void setDependencyCountForClass(String className, int countOfDependencies);

    void setInternalInstancesCountForMethod(String methodName, int countOfInternalInstances);

}
