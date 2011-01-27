package org.creativelabs;

import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: azotcsit
 * Date: 27.01.11
 * Time: 14:26
 * To change this template use File | Settings | File Templates.
 */
public class AssignVisitor extends VoidVisitorAdapter<List<String>>{

    @Override
    public void visit(AssignExpr n, List<String> arg) {
        if (n.getTarget() instanceof NameExpr){
            arg.add(((NameExpr)n.getTarget()).getName());
        }
    }
}
