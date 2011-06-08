package org.creativelabs.introspection;

import org.creativelabs.copy.CopyingUtils;

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

    @Override
    public <ClassTypeStub> ClassTypeStub copy() {
        return CopyingUtils.<ClassTypeStub>copy(this);
    }
}
