package org.creativelabs.report;

import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.typefinder.Dependency;
import org.testng.annotations.*;

import java.util.*;

import static org.mockito.Mockito.*;

public class DataCollectorTest {

    Map<String, Collection<Dependency>> deps;
    Map<String, InternalInstancesGraph>  intInstances;

    DataCollector dataCollector;
    ReportBuilder mockBuilder;

    @BeforeMethod
    public void setUp() {
        dataCollector = new DataCollector();
        mockBuilder = mock(ReportBuilder.class);

        deps = new HashMap<String, Collection<Dependency>>();
        intInstances = new HashMap<String, InternalInstancesGraph>();
    }

    @Test
    public void testBuildReport() {
        dataCollector.setDependencies("className", deps);
        dataCollector.setInternalInstances("className", intInstances);

        dataCollector.buildReport(mockBuilder);

        verify(mockBuilder).setDependencies(eq("className"), eq(deps));
        verify(mockBuilder).setInternalInstances(eq("className"), eq(intInstances));
    }

    @Test
    public void testBuildReportOppositeOrder() {
        dataCollector.setInternalInstances("className", intInstances);
        dataCollector.setDependencies("className", deps);

        dataCollector.buildReport(mockBuilder);

        verify(mockBuilder).setDependencies(eq("className"), eq(deps));
        verify(mockBuilder).setInternalInstances(eq("className"), eq(intInstances));
    }

}
