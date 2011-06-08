package org.creativelabs.report;

import org.creativelabs.copy.CopyingUtils;
import org.creativelabs.introspection.*;
import org.creativelabs.typefinder.Dependency;
import org.testng.annotations.*;

import java.util.*;

import static org.testng.AssertJUnit.*;
import static org.mockito.Mockito.*;

public class ErrorCountDependencyReportTest {

    private class ClassTypeErrorImpl implements ClassType, ClassTypeError {
        public String toString() {
            return "";
        }

        public String getShortString() {
            return "";
        }

        @Override
        public <ClassTypeErrorImpl> ClassTypeErrorImpl copy() {
            return CopyingUtils.<ClassTypeErrorImpl>copy(this);
        }
    }

    @BeforeMethod
    public void setUp() {

    } 

    @Test
    public void testClassTypeErrorCount() {
        ErrorCountDependencyReport report = new ErrorCountDependencyReport();
        ArrayList<Dependency> list = new ArrayList<Dependency>();
        list.add(new Dependency("error", new ClassTypeErrorImpl()));
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
        list.add(new Dependency("error", new ClassTypeErrorImpl()));
        list.add(new Dependency("normal", mock(ClassType.class)));
        HashMap<String, Collection<Dependency>> map = new HashMap<String, Collection<Dependency>>();
        map.put("foo", list);

        report.setDependencies("ClassName", map);

        List<String> errors = report.getErrorMessages();

        assertEquals(1, errors.size());
        assertEquals("Error in : ClassName : error", errors.get(0));
    }
}
