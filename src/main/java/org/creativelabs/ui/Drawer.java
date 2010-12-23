package org.creativelabs.ui;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: azotcsit
 * Date: 23.12.10
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
public interface Drawer {

    public void draw(int width, int height, JFrame frame);

    public void saveToFile(int width, int height, String fileName);

}
