package org.creativelabs.ui.jung;

import org.creativelabs.Vertex;

public class JungVertex implements Vertex {

    private String label;

    public JungVertex() {
    }

    public JungVertex(String label) {
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
