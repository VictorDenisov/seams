package org.creativelabs;

import japa.parser.ast.expr.Expression;

class Dependency {

    String expression;

    String type;

    Dependency(String expression, String type) {
        this.expression = expression;
        this.type = type;
    }

}
