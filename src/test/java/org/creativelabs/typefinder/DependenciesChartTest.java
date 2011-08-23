package org.creativelabs.typefinder;

import org.creativelabs.chart.ToStringChartBuilder;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertEquals;

public class DependenciesChartTest {

    private String dependenciesChartToString(DependenciesChart chart){
        ToStringChartBuilder builder = new ToStringChartBuilder();
        chart.buildChart(builder);
        return builder.toString();
    }

    @Test
    public void testConstructor() {
        new DependenciesChart();
    }

    @Test
    public void testAddDependenciesCountForClass() {
        DependenciesChart chart = new DependenciesChart();
        chart.addDependenciesCountForClass("className", 2);
        assertEquals("{className -> 2, } {}", dependenciesChartToString(chart));
    }

    @Test
    public void testAddInternalInstancesCountForClass() {
        DependenciesChart chart = new DependenciesChart();
        chart.addInternalInstancesCountForMethod("methodName", 2);
        assertEquals("{} {methodName -> 2, }", dependenciesChartToString(chart));
    }

    @Test
    public void testAddDependenciesCountForClassAndAddInternalInstancesCountForClass() {
        DependenciesChart chart = new DependenciesChart();
        chart.addDependenciesCountForClass("className", 2);
        chart.addInternalInstancesCountForMethod("methodName", 2);
        assertEquals("{className -> 2, } {methodName -> 2, }", dependenciesChartToString(chart));
    }
}
