package org.creativelabs.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class BarChartBuilder implements ChartBuilder {

    private static final String METRICS = "Dependencies and Internal instances";
    private static final String X_AXIS_LABEL = "Metrics";
    private static final String Y_AXIS_LABEL = "Count";
    private static final String TITLE = "Count of objects by metrics";
    private static final String CLASS = "Class: ";
    private static final String METHOD = "Method: ";

    private DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

    @Override
    public void setDependencyCountForClass(String className, int countOfDependencies) {
        dataSet.addValue(countOfDependencies, METRICS, CLASS + className);
    }

    @Override
    public void setInternalInstancesCountForMethod(String methodName, int countOfInternalInstances) {
        dataSet.addValue(countOfInternalInstances, METRICS, METHOD + methodName);
    }

    public JFreeChart getChart() {
        return ChartFactory.createBarChart3D(
                TITLE,
                X_AXIS_LABEL,
                Y_AXIS_LABEL,
                this.dataSet,
                PlotOrientation.VERTICAL,
                true, true, false);
    }

}
