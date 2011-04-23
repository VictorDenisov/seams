package org.creativelabs.ssa;

import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.Set;

/**
 * Finds names of variables which are modifying into the visited code.
 */
public class AssignVisitor extends VoidVisitorAdapter<Set<String>> {

    @Override
    public void visit(AssignExpr n, Set<String> arg) {
        Expression expression = n.getTarget();
        if (expression instanceof NameExpr) {
            arg.add(((NameExpr) expression).getName());
        }
    }
}
