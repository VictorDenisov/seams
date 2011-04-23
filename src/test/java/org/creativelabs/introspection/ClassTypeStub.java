package org.creativelabs.introspection;

public class ClassTypeStub implements ClassType {

    private String className;

    public ClassTypeStub(String className) {
        this.className = className;
    }

    public String toStringRepresentation() {
        return className;
    }

    @Override
    public String toString() {
        return toStringRepresentation();
    }

    @Override
    public String getShortString() {
        return toStringRepresentation();
    }
}
