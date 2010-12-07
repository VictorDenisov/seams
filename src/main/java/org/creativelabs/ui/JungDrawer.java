package org.creativelabs.ui;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.*;
import org.creativelabs.Dependency;
import org.creativelabs.ui.jung.Vertex;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Set;


/**
 * Created by IntelliJ IDEA.
 * User: azotcsit
 * Date: 05.12.2010
 * Time: 17:32:22
 * To change this template use File | Settings | File Templates.
 */
public class JungDrawer {

    public JungDrawer(Map<String, Set<Dependency>> dependencies, String fileName) {
        // Graph<V, E> where V is the type of the vertices and E is the type of the edges
        Graph<Vertex<String>, String> g = new SparseMultigraph<Vertex<String>, String>();
        for (Map.Entry<String, Set<Dependency>> entry : dependencies.entrySet()) {
            String depsName = entry.getKey();
            Vertex<String> root = new Vertex<String>(depsName, depsName);
            g.addVertex(root);
            for (Dependency value : entry.getValue()) {
                if (value.getType() != null) {
                    String type = value.getType();
                    Vertex<String> node = new Vertex<String>(depsName + type, type);
                    if (!g.getVertices().contains(node)){
                        g.addVertex(node);
                        g.addEdge(root.getId() + node.getId(), root, node, EdgeType.DIRECTED);
                    }
                    else{
                        if (!g.getEdges(EdgeType.DIRECTED).contains(root.getId() + node.getId())){
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

        JFrame frame = new JFrame(fileName.substring(fileName.lastIndexOf("/")));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }
}
