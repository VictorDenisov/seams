package org.creativelabs.ui;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ChartDrawer implements Drawer{

    private JFreeChart chart = null;

    public ChartDrawer(JFreeChart chart) {
        this.chart = chart;
    }

    @Override
    public void draw(int width, int height, JFrame frame) {
        chart.draw((Graphics2D) frame.getGraphics(), new Rectangle(0, 0, width, height));
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void saveToFile(int width, int height, String fileName) {
        try {
            ChartUtilities.saveChartAsJPEG(new File(fileName + ".jpg"), chart, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
