package org.creativelabs.report;

import java.util.*;
import java.io.*;
import org.creativelabs.*;
import org.creativelabs.ui.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class OverviewChartReportBuilder implements ReportBuilder {

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

    public JFreeChart getChart() throws Exception {
        if (dependencyCount.keySet().size() != internalInstancesCount.keySet().size()) {
            throw new IllegalStateException();
        }

        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

        for (String className : dependencyCount.keySet()) {
            Map<String, Integer> depCountByMethod = dependencyCount.get(className);
            Map<String, Integer> intInstanceByMethod = internalInstancesCount.get(className);
            if (depCountByMethod.keySet().size() != intInstanceByMethod.keySet().size()) {
                throw new IllegalStateException();
            }
            for (String methodName : depCountByMethod.keySet()) {
                dataSet.addValue(depCountByMethod.get(methodName), 
                        "dependency count", className + "." + methodName);
                dataSet.addValue(intInstanceByMethod.get(methodName), 
                        "internal instances", className + "." + methodName);
            }
        }
        return ChartFactory.createBarChart(
                "Overview chart",
                "Methods",
                "Value",
                dataSet,
                PlotOrientation.VERTICAL,
                true, true, false);
    }

    public void saveToFile(String name) throws Exception {
            final int CHART_WIDTH = 3000;
            final int CHART_HEIGHT = 500;
            new ChartDrawer(getChart())
                .saveToFile(CHART_WIDTH, CHART_HEIGHT, name);
    }
}
