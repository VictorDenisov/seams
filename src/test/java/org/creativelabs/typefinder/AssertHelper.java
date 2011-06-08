package org.creativelabs.typefinder;

import java.util.List;

import static org.testng.AssertJUnit.*;

public class AssertHelper {
    public static void assertEqualsList(List<String> expected, List<String> received) {
        assertNotNull(received);
        assertEquals(expected.size(), received.size());
        int n = expected.size();
        for (int i = 0; i < n; ++i) {
            assertEquals(expected.get(i), received.get(i));
        }
    }
}
