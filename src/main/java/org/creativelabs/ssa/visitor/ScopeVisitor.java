package org.creativelabs.ssa.visitor;

import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.EnclosedExpr;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.visitor.GenericVisitorAdapter;
import org.creativelabs.helper.GenericVisitorHelper;

/**
 * @author azotcsit
 *         Date: 09.08.11
 *         Time: 23:44
 */
public class ScopeVisitor extends GenericVisitorAdapter<String, Object> {
    @Override
    public String visit(NameExpr n, Object arg) {
        return n.getName();
    }

    @Override
    public String visit(FieldAccessExpr n, Object arg) {
        return GenericVisitorHelper.visitExpression(n.getScope(), arg, this) + "." + n.getField();
    }

    @Override
    public String visit(CastExpr n, Object arg) {
        return GenericVisitorHelper.visitExpression(n.getExpr(), arg, this);
    }

    @Override
    public String visit(EnclosedExpr n, Object arg) {
        return GenericVisitorHelper.visitExpression(n.getInner(), arg, this);
    }


}
