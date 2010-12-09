package org.creativelabs.graph;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import org.creativelabs.Dependency;
import org.creativelabs.ui.JungDrawer;
import org.creativelabs.ui.jung.Vertex;

import javax.swing.*;
import java.awt.*;
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
        Graph<Vertex<String>, String> g = new SparseMultigraph<Vertex<String>, String>();
        for (Map.Entry<String, Set<String>> entry : methodInternalInstances.entrySet()) {
            String depsName = entry.getKey();
            Vertex<String> root = new Vertex<String>(depsName, depsName);
            if (!entry.getValue().isEmpty()) {
                g.addVertex(root);
            }
            for (String value : entry.getValue()) {
                if (value != null) {
                    Vertex<String> node = new Vertex<String>(depsName + value, value);
                    if (!g.getVertices().contains(node)) {
                        g.addVertex(node);
                        g.addEdge(root.getId() + node.getId(), root, node, EdgeType.DIRECTED);
                    } else {
                        if (!g.getEdges(EdgeType.DIRECTED).contains(root.getId() + node.getId())) {
                            g.addEdge(root.getId() + node.getId(), root, node, EdgeType.DIRECTED);
                        }
                    }

                }

            }
        }
        // The Layout<V, E> is parameterized by the vertex and edge types
        Layout<Vertex<String>, String> layout = new SpringLayout<Vertex<String>, String>(g);
        layout.setSize(new Dimension(300, 300)); // sets the initial size of the space
        // The BasicVisualizationServer<V,E> is parameterized by the edge types
        BasicVisualizationServer<Vertex<String>, String> vv =
                new BasicVisualizationServer<Vertex<String>, String>(layout);
        vv.setPreferredSize(new Dimension(350, 350)); //Sets the viewing area size
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Vertex<String>>());

        JFrame frame = new JFrame(className.substring(className.lastIndexOf("/")));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
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
