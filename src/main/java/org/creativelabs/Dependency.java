package org.creativelabs;

public class Dependency {

    private String expression;

    private String type;

    Dependency(String expression, String type) {
        this.expression = expression;
        this.type = type;
    }

    String getExpression() {
        return expression;
    }

    public String getType() {
        return type;
    }
}
