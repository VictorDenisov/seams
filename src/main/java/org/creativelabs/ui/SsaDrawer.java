package org.creativelabs.ui;

import org.creativelabs.ssa.SsaFormAstRepresentation;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Saves ssa representation of method to the file.
 */
public class SsaDrawer implements Drawer {

    private SsaFormAstRepresentation form;

    public SsaDrawer(SsaFormAstRepresentation form) {
        this.form = form;
    }

    @Override
    public void draw(int width, int height, JFrame frame) {
        throw new UnsupportedOperationException("Draw operation is not supported by SsaDrawer.");
    }

    @Override
    public void saveToFile(int width, int height, String fileName) {
        try {
            PrintWriter writer = new PrintWriter(fileName + ".txt");
            writer.print(form.getAst().toString());
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
