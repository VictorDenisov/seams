package org.creativelabs.drawer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import org.apache.commons.collections15.Transformer;
import org.creativelabs.graph.Vertex;
import org.creativelabs.graph.condition.bool.FalseBooleanCondition;

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

    private BasicVisualizationServer<Vertex, String> addContentToFrame(JFrame frame) {
        // The Layout<V, E> is parameterized by the vertex and edge types
        Layout<Vertex, String> layout = new TreeLayout<Vertex, String>((Forest<Vertex, String>)g, 400, 150);
        // The BasicVisualizationServer<V,E> is parameterized by the edge types

        Transformer<Vertex, Paint> vertexPaint = new Transformer<Vertex,Paint>() {
            @Override
            public Paint transform(Vertex vertex) {
                if (vertex.getInternalCondition() instanceof FalseBooleanCondition) {
                    return Color.GREEN;
                } else if (vertex.getExternalCondition() instanceof FalseBooleanCondition) {
                    return Color.RED;
                } else {
                    return Color.WHITE;
                }
            }
        };

        Transformer<String,Font> edgeFontTransformer =
                new Transformer<String,Font>() {
            @Override
            public Font transform(String text) {
                return new Font("Helvetica", Font.PLAIN, 8);
            }
        };


        Transformer<Vertex, String> vertexLabeller = new getLabelLabeller<Vertex>();

        BasicVisualizationServer<Vertex, String> vv =
                new BasicVisualizationServer<Vertex, String>(layout);
        vv.setPreferredSize(layout.getSize()); //Sets the viewing area size

        vv.getRenderContext().setVertexLabelTransformer(vertexLabeller);
        vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
        vv.getRenderContext().setEdgeFontTransformer(edgeFontTransformer);

        frame.getContentPane().add(vv);
        frame.pack();
        return vv;
    }

    @Override
    public void draw(int width, int height, JFrame frame) {
        addContentToFrame(frame);
        frame.setVisible(true);
    }

    @Override
    public void saveToFile(int width, int height, String fileName) {
        JFrame frame = new JFrame(fileName);
        JPanel panel = addContentToFrame(frame);

        //if pickture would be bigger
        if (panel.getWidth() > 13000) {
            width = 13000;
        } else {
            width = panel.getWidth();
        }

        BufferedImage image = new BufferedImage(width , panel.getHeight(), 5);

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
