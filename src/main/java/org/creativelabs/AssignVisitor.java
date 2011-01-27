package org.creativelabs;

import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

public class AssignVisitor extends VoidVisitorAdapter<List<String>> {

    @Override
    public void visit(AssignExpr n, List<String> arg) {
        Expression expression = n.getTarget();
        if (expression instanceof NameExpr) {
            arg.add(((NameExpr) expression).getName());
        }
    }
}
