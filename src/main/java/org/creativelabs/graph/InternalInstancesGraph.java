package org.creativelabs.graph;

import org.creativelabs.ui.JungDrawer;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: azotcsit
 * Date: 09.12.2010
 * Time: 18:53:18
 * To change this template use File | Settings | File Templates.
 */
public class InternalInstancesGraph {

    private final int FRAME_WIDTH = 350;
    private final int FRAME_HEIGHT = 350;

    //TODO think about internal instances in init static block
    //TODO delete JungDrawer class

    private Map<String, Set<String>> methodInternalInstances = new HashMap<String, Set<String>>();

    private String className;

    public InternalInstancesGraph(String className) {
        this.className = className;
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

    public void draw(){
        JFrame frame = new JFrame(className);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        new JungDrawer(methodInternalInstances, frame, FRAME_WIDTH, FRAME_HEIGHT);
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
