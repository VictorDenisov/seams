package org.creativelabs.chart;

public interface ChartBuilder {

    public void setDependencyCountForClass(String className, int countOfDependencies);

    public void setInternalInstancesCountForMethod(String methodName, int countOfInternalInstances);

}
