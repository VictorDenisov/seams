package org.creativelabs.ui.jung;

/**
 * Created by IntelliJ IDEA.
 * User: azotcsit
 * Date: 07.12.2010
 * Time: 13:33:52
 * To change this template use File | Settings | File Templates.
 */
public class Vertex<IdType> {
    IdType id;
    String label;

    public Vertex() {
    }

    public Vertex(IdType id, String label) {
        this.id = id;
        this.label = label;
    }

    public IdType getId() {
        return id;
    }

    public void setId(IdType idType) {
        this.id = idType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vertex vertex = (Vertex) o;

        if (id != null ? !id.equals(vertex.id) : vertex.id != null) return false;
        if (label != null ? !label.equals(vertex.label) : vertex.label != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return label;
    }
}
