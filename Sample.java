package temp;

import java.io.File;

import log4j.logger.Logger;
import org.sample.SampleClass;

public class Sample {
    
    private int v;

    private int myField;

    private SampleClass sampleField;

    public Sample(int v) {
        this.v = v;
    }
    
    public void doSomething() {
        int v;
        myField = 4; //Local instance
        sampleField = new SampleClass(); // Local instance
        SampleClass sampleLocalVariable = Logger.getInstance(); //External instance
        SampleClass newSampleLocalVariable = sampleField; // Local instance. sampleFields is local instance.
        for (int i = 0; i < n; ++i) {
            System.out.println("Hello world"); //A Seam
            Logger.getLogger().warn("Hello"); //A Seam
            sampleField.doIt(); // not a seam. sampleField has local instance
            sampleLocalVariable.getWarn(); // A Seam
        }
        newSampleLocalVariable.helloAll(); // Not a seam
        getV().processValue(); // A seam
    }

    public int getV() {
        return v;
    }
    
    public void setV(int v) {
        this.v = v;
    }
}
