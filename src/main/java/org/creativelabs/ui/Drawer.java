package org.creativelabs.ui;

import javax.swing.*;

public interface Drawer {

    public void draw(int width, int height, JFrame frame);

    public void saveToFile(int width, int height, String fileName);

}
