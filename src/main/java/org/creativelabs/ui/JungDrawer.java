package org.creativelabs.ui;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import org.creativelabs.ui.jung.Vertex;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Set;

public class JungDrawer {

    public JungDrawer(Map<String, Set<String>> dependencies, JFrame frame, int width, int height) {
        // Graph<V, E> where V is the type of the vertices and E is the type of the edges
        Graph<Vertex, String> g = new SparseMultigraph<Vertex, String>();
        for (Map.Entry<String, Set<String>> entry : dependencies.entrySet()) {
            String depsName = entry.getKey();
            Vertex root = new Vertex(depsName);
            if (!entry.getValue().isEmpty()) {
                g.addVertex(root);
            }
            for (String value : entry.getValue()) {
                if (value != null) {
                    Vertex node = new Vertex(value);
                    if (!g.getVertices().contains(node)) {
                        g.addVertex(node);
                        g.addEdge(root.toString() + node.toString(), root, node, EdgeType.DIRECTED);
                    } else {
                        if (!g.getEdges(EdgeType.DIRECTED).contains(root.toString() + node.toString())) {
                            g.addEdge(root.toString() + node.toString(), root, node, EdgeType.DIRECTED);
                        }
                    }

                }

            }
        }
        // The Layout<V, E> is parameterized by the vertex and edge types
        Layout<Vertex, String> layout = new KKLayout<Vertex, String>(g);
        layout.setSize(new Dimension(width, height)); // sets the initial size of the space
        // The BasicVisualizationServer<V,E> is parameterized by the edge types
        BasicVisualizationServer<Vertex, String> vv =
                new BasicVisualizationServer<Vertex, String>(layout);
        vv.setPreferredSize(new Dimension(width, height)); //Sets the viewing area size
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Vertex>());

        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }
}
