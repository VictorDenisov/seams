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

public class ErrorCountDependencyReportTest {

    private class ClassTypeError implements ClassType {
        public String toString() {
            return "";
        }

        public String getShortString() {
            return "";
        }
    }

    @BeforeMethod
    public void setUp() {

    } 

    @Test
    public void testClassTypeErrorCount() {
        ErrorCountDependencyReport report = new ErrorCountDependencyReport();
        ArrayList<Dependency> list = new ArrayList<Dependency>();
        list.add(new Dependency("error", new ClassTypeError()));
        list.add(new Dependency("normal", mock(ClassType.class)));
        HashMap<String, Collection<Dependency>> map = new HashMap<String, Collection<Dependency>>();
        map.put("foo", list);

        report.setDependencies(null, map);
        assertEquals(1, report.getCount());
    }

    @Test
    public void testClassTypeListOfErrors() {
        ErrorCountDependencyReport report = new ErrorCountDependencyReport();
        ArrayList<Dependency> list = new ArrayList<Dependency>();
        list.add(new Dependency("error", new ClassTypeError()));
        list.add(new Dependency("normal", mock(ClassType.class)));
        HashMap<String, Collection<Dependency>> map = new HashMap<String, Collection<Dependency>>();
        map.put("foo", list);

        report.setDependencies("ClassName", map);

        List<String> errors = report.getErrorMessages();

        assertEquals(1, errors.size());
        assertEquals("Error in : ClassName : error", errors.get(0));
    }
}
