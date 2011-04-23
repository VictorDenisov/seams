package org.creativelabs.chart;

import com.google.inject.internal.ToStringBuilder;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertEquals;

public class ToStringChartBuilderTest {

    @Test
    public void testConstructor(){
        new ToStringChartBuilder();
    }

    @Test
    public void testSetDependencyCountForClass(){
        ToStringChartBuilder builder = new ToStringChartBuilder();
        builder.setDependencyCountForClass("className", 2);
        assertEquals("{className -> 2, } {}", builder.toString());
    }

    @Test
    public void testSetInternalInstancesCountForMethod(){
        ToStringChartBuilder builder = new ToStringChartBuilder();
        builder.setInternalInstancesCountForMethod("methodName", 2);
        assertEquals("{} {methodName -> 2, }", builder.toString());
    }

    @Test
    public void testSetInternalInstancesCountForMethodAndSetInternalInstancesCountForMethod(){
        ToStringChartBuilder builder = new ToStringChartBuilder();
        builder.setDependencyCountForClass("className", 2);
        builder.setInternalInstancesCountForMethod("methodName", 2);
        assertEquals("{className -> 2, } {methodName -> 2, }", builder.toString());
    }
}
