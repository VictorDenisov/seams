package org.creativelabs;

import org.creativelabs.introspection.*;

public class Dependency {

    private String expression;

    private ClassType type;

    Dependency(String expression, ClassType type) {
        this.expression = expression;
        this.type = type;
    }

    String getExpression() {
        return expression;
    }

    public ClassType getType() {
        return type;
    }
}
