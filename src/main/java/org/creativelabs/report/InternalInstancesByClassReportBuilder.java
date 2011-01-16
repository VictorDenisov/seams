package org.creativelabs.report;

import java.util.*;
import java.io.*;
import org.creativelabs.*;
import org.creativelabs.ui.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class InternalInstancesByClassReportBuilder implements ReportBuilder {

    protected Map<String, Integer> internalInstancesCount = new HashMap<String, Integer>();

    public void setDependencies(String className, Map<String, Collection<Dependency>> dependencies) {
    }

    public void setInternalInstances(String className, Map<String, InternalInstancesGraph> instances) {
        int answer  = 0;
        for (Map.Entry<String, InternalInstancesGraph> entry : instances.entrySet()) {
            answer += entry.getValue().toSet().size();
        }
        internalInstancesCount.put(className, answer);
    }

    public JFreeChart getChart() throws Exception {
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

        for (String className : internalInstancesCount.keySet()) {
            int value = internalInstancesCount.get(className);
            dataSet.addValue(value, "internal instances", className);
            
        }
        return ChartFactory.createBarChart(
                "Internal instances per class",
                "Classes",
                "Value",
                dataSet,
                PlotOrientation.VERTICAL,
                true, true, false);
    }

    public void saveToFile(String name) throws Exception {
            final int CHART_WIDTH = 1000;
            final int CHART_HEIGHT = 500;
            new ChartDrawer(getChart())
                .saveToFile(CHART_WIDTH, CHART_HEIGHT, name);
    }
}
