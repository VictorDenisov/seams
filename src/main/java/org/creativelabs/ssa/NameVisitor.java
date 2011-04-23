package org.creativelabs.ssa;

import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.Set;

/**
 * Finds names of variables which are using into the visited code.
 */
public class NameVisitor extends VoidVisitorAdapter<Set<String>> {

    @Override
    public void visit(NameExpr n, Set<String> arg) {
            arg.add(n.getName());
    }

    @Override
    public void visit(VariableDeclaratorId n, Set<String> arg) {
        arg.add(n.getName());
    }
}
