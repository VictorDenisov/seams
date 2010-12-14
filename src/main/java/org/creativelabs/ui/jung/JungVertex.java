package org.creativelabs.ui.jung;

import org.creativelabs.Vertex;

public class JungVertex implements Vertex {

    private static int idCount = 0;

    private int id;

    private String label;

    public JungVertex() {
    }

    public JungVertex(String label) {
        this.id = id;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
