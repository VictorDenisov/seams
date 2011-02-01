package org.creativelabs.ui;

import org.creativelabs.ssa.SimpleSsaForm;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class SsaDrawer implements Drawer {

    private SimpleSsaForm form;

    public SsaDrawer(SimpleSsaForm form) {
        this.form = form;
    }

    @Override
    public void draw(int width, int height, JFrame frame) {
        //TODO implement drawing ssa form
    }

    @Override
    public void saveToFile(int width, int height, String fileName) {
        try {
            PrintWriter writer = new PrintWriter(fileName + ".txt");
            writer.print(form.getForm());
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
