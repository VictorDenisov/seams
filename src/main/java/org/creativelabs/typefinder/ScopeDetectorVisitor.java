package org.creativelabs.typefinder;

import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class ScopeDetectorVisitor extends VoidVisitorAdapter<Object> {

    private String deepestCall;

    public String getName() {
        return deepestCall;
    }

    @Override
    public void visit(NameExpr n, Object o) {
        deepestCall = n.getName();
    }

    @Override
    public void visit(MethodCallExpr n, Object o) {
        deepestCall = n.getName();
        super.visit(n, o);
    }

    @Override
    public void visit(FieldAccessExpr n, Object o) {
        deepestCall = n.getField();
        super.visit(n, o);
    }
}
