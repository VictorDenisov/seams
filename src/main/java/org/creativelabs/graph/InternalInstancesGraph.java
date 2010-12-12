package org.creativelabs.graph;

import org.creativelabs.ui.JungDrawer;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InternalInstancesGraph {

    private static final int FRAME_WIDTH = 350;
    private static final int FRAME_HEIGHT = 350;

    //TODO think about internal instances in init static block
    //TODO delete JungDrawer class

    private Map<String, Set<String>> methodInternalInstances = new HashMap<String, Set<String>>();

    private String fileName;

    public InternalInstancesGraph(String fileName) {
        this.fileName = fileName;
    }

    public void addMethodInternalInstances(String methodName, Set<String> dependencies) {
        if (methodName == null || methodName.isEmpty()) {
            throw new IllegalArgumentException("Argument methodName is illegal.");
        }
        if (dependencies == null) {
            throw new IllegalArgumentException("Argument dependencies is illegal.");
        }
        methodInternalInstances.put(methodName, dependencies);
    }

    public void setMethodInternalInstances(Map<String, Set<String>> methodInternalInstances) {
        this.methodInternalInstances = methodInternalInstances;
    }

    public Map<String, Set<String>> getMethodInternalInstances() {
        return methodInternalInstances;
    }

    public void draw() {
        JFrame frame = new JFrame(fileName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JungDrawer drawer = new JungDrawer(methodInternalInstances);
        drawer.draw(frame, FRAME_WIDTH, FRAME_HEIGHT);
    }

    public void saveToFile() {
        JFrame frame = new JFrame(fileName);
        JungDrawer drawer = new JungDrawer(methodInternalInstances);
        drawer.saveToFile(frame, FRAME_WIDTH, FRAME_HEIGHT, fileName);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("InternalInstancesGraph{");
        for (String methodName : methodInternalInstances.keySet()) {
            buffer.append("\n method name = " + methodName + " -> internal instances = {\n");
            for (String internalInstances : methodInternalInstances.get(methodName)) {
                buffer.append("  " + internalInstances + "\n");
            }
            buffer.append(" }\n");
        }
        buffer.append('}');
        return buffer.toString();
    }
}
