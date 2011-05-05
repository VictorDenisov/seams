package org.creativelabs.drawer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class GraphvizDrawer implements Drawer {

    private static final int SUCCESS = 0;

    @Override
    public void draw(int width, int height, JFrame frame) {
        //no operations
    }

    @Override
    public void saveToFile(int width, int height, String fileName) {
        try {
            new ToPngConverter().convert(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            File dotFile = new File(fileName + ".dot");
            dotFile.delete();
        }
    }

    private class ToPngConverter {
        private static final String LINUX = "Linux";
        private static final String WINDOWS = "Windows";

        public void convert(String fileName) throws IOException, InterruptedException {
            Process convertingToPng = Runtime.getRuntime().exec(getArgs(fileName));

            int status = convertingToPng.waitFor();
            if (status != SUCCESS) {
                throw new IOException("Could not convert dot file " + fileName + ".dot to png.");
            }
        }

        private String[] getArgs(String fileName) {
            if (LINUX.equals(System.getProperty("os.name"))) {
                return new String[]{"/bin/sh",
                        "-c",
                        "dot -Tpng " + fileName + ".dot > " + fileName + ".png"};
            } else if (WINDOWS.equals(System.getProperty("os.name"))) {
                //TODO to implement
            }

            throw new IllegalArgumentException("Could not process " +
                    System.getProperty("os.name") +
                    "os type.");
        }

    }
}
