package org.creativelabs;

import org.creativelabs.introspection.*;

public class Dependency {

    private String expression;

    private ClassType type;

    public Dependency(String expression, ClassType type) {
        this.expression = expression;
        this.type = type;
    }

    public String getExpression() {
        return expression;
    }

    public ClassType getType() {
        return type;
    }
}
