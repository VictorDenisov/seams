package org.creativelabs.ui;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import org.creativelabs.graph.Vertex;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JungDrawer implements Drawer {

    // Graph<V, E> where V is the type of the vertices and E is the type of the edges
    private Graph<Vertex, String> g = null;

    public JungDrawer(Graph<Vertex, String> g) {
        this.g = g;
    }

    private BasicVisualizationServer<Vertex, String> addContentToFrame(JFrame frame, int width, int height) {
        // The Layout<V, E> is parameterized by the vertex and edge types
        Layout<Vertex, String> layout = new KKLayout<Vertex, String>(g);
        layout.setSize(new Dimension(width, height)); // sets the initial size of the space
        // The BasicVisualizationServer<V,E> is parameterized by the edge types
        BasicVisualizationServer<Vertex, String> vv =
                new BasicVisualizationServer<Vertex, String>(layout);
        vv.setPreferredSize(new Dimension(width, height)); //Sets the viewing area size
        vv.getRenderContext().setVertexLabelTransformer(new getLabelLabeller<Vertex>());

        frame.getContentPane().add(vv);
        frame.pack();
        return vv;
    }

    @Override
    public void draw(int width, int height, JFrame frame) {
        addContentToFrame(frame, width, height);
        frame.setVisible(true);
    }

    @Override
    public void saveToFile(int width, int height, String fileName) {
        JFrame frame = new JFrame(fileName);
        JPanel panel = addContentToFrame(frame, width, height);

        BufferedImage image = new BufferedImage(width, height, 1);
        Graphics graphics = image.getGraphics();
        panel.paint(graphics);
         try {
            ImageIO.write(image, "jpg", new File(fileName + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.dispose();
    }
}
