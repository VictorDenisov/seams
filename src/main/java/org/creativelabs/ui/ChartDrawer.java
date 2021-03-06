package org.creativelabs.ui;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ChartDrawer implements Drawer {

    private JFreeChart chart = null;

    public ChartDrawer(JFreeChart chart) {
        this.chart = chart;
        CategoryAxis ca = ((CategoryPlot)chart.getPlot()).getDomainAxis();
        ca.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.5235));
    }

    @Override
    public void draw(int width, int height, JFrame frame) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(width, height));
        frame.getContentPane().add(chartPanel);
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
