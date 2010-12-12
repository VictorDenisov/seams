package org.creativelabs.ui.jung;

public class Vertex {

    private static int idCount = 0;

    private int id;

    private String label;

    public Vertex() {
    }

    public Vertex(String label) {
        this.id = id;
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
