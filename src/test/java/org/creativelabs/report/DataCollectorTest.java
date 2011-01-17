package org.creativelabs.report;

import org.creativelabs.*;
import org.creativelabs.graph.*;
import org.creativelabs.introspection.*;
import org.testng.annotations.*;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;

import java.util.*;

import static org.testng.AssertJUnit.*;
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
