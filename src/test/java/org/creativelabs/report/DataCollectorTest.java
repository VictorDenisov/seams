package org.creativelabs.report;

import org.creativelabs.*;
import org.creativelabs.graph.*;
import org.creativelabs.introspection.*;
import org.testng.annotations.Test;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;

import java.util.*;

import static org.testng.AssertJUnit.*;
import static org.mockito.Mockito.*;

public class DataCollectorTest {

    @Test
    public void testBuildReport() {
        DataCollector dataCollector = new DataCollector();
        ReportBuilder mockBuilder = mock(ReportBuilder.class);

        Map<String, Collection<Dependency>> deps = new HashMap<String, Collection<Dependency>>();
        Map<String, InternalInstancesGraph> intInstances 
            = new HashMap<String, InternalInstancesGraph>();
        dataCollector.setDependencies("className", deps);
        dataCollector.setInternalInstances("className", null);

        dataCollector.buildReport(mockBuilder);
        verify(mockBuilder).setDependencies(eq("className"), eq(deps));
    }

}
