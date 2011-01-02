package org.creativelabs.ui;

import javax.swing.*;

public interface Drawer {

    void draw(int width, int height, JFrame frame);

    void saveToFile(int width, int height, String fileName);

}
