package org.creativelabs;

import japa.parser.ast.expr.Expression;

class Dependency {

    private String expression;

    private String type;

    Dependency(String expression, String type) {
        this.expression = expression;
        this.type = type;
    }

    String getExpression() {
        return expression;
    }

    String getType() {
        return type;
    }
}
