package org.creativelabs.samples;

public class SampleOS {

    private static native boolean is(int type);

    public static final boolean IS_UNIX    = is(4);
}
