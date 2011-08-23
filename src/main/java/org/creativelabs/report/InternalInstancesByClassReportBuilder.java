package org.creativelabs.report;

import org.creativelabs.drawer.ChartDrawer;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.ssa.SsaError;
import org.creativelabs.ssa.representation.SsaFormRepresentation;
import org.creativelabs.typefinder.Dependency;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.*;

public class InternalInstancesByClassReportBuilder implements ReportBuilder {

    private static class Pair implements Comparable<Pair> {
        private String name;

        private int count;

        private Pair(String name, int count) {
            this.name = name;
            this.count = count;
        }

        public int compareTo(Pair p) {
            if (count < p.count) {
                return -1;
            }
            if (count > p.count) {
                return 1;
            }
            return 0;
        }
    }

    protected List<Pair> internalInstancesCount = new ArrayList<Pair>();

    public void setDependencies(String className, Map<String, Collection<Dependency>> dependencies) {
    }

    public void setInternalInstances(String className, Map<String, InternalInstancesGraph> instances) {
        int answer  = 0;
        for (Map.Entry<String, InternalInstancesGraph> entry : instances.entrySet()) {
            answer += entry.getValue().toSet().size();
        }
        internalInstancesCount.add(new Pair(className, answer));
    }

    @Override
    public void setSsaFormRepresentations(String className, Set<SsaFormRepresentation> ssaFormRepresentations) {
    }

    @Override
    public void setSsaErrors(String className, Set<SsaError> ssaErrors) {
    }
    /*
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
    */
    private int count = 0;
    private int bunchNumber = 0;
    private DefaultCategoryDataset dataSet;
    private final int CHART_WIDTH = 1000;
    private final int CHART_HEIGHT = 500;

    private void outputChart(String name) throws Exception {
        ++bunchNumber;
        count = 0;

        JFreeChart chart = ChartFactory.createBarChart(
                "Internal instances per class",
                "Classes",
                "Value",
                dataSet,
                PlotOrientation.VERTICAL,
                true, true, false);

        new ChartDrawer(chart)
            .saveToFile(CHART_WIDTH, CHART_HEIGHT, name + bunchNumber);

        dataSet = new DefaultCategoryDataset();
    }

    public void saveToFile(String name) throws Exception {
        Collections.sort(internalInstancesCount);
        dataSet = new DefaultCategoryDataset();
        count = 0;
        bunchNumber = 0;

        for (Pair pair : internalInstancesCount) {
            String className = pair.name;
            int value = pair.count;

            dataSet.addValue(value, "internal instances", className);

            ++count;
            if (count == 30) {
                outputChart(name);
            }
            
        }
        if (count > 0) {
            outputChart(name);
        }
    }
}
