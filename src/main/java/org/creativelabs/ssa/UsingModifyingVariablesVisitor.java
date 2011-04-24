package org.creativelabs.ssa;

import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

/**
 * Stores using and modifying variables in statements to statements.
 *
 * @author azotcsit
 *         Date: 23.04.11
 *         Time: 20:44
 */
public class UsingModifyingVariablesVisitor extends VoidVisitorAdapter {
    //TODO implement all functionality

    public void visit(BlockStmt body) {
        List<Statement> statements =  body.getStmts();
        if (statements != null) {
            for (Statement statement : statements) {
//                visit(statement);
                //TOOD visit statement
                ((Statement) body).addUsingVariables(statement.getUsingVariables());
                ((Statement) body).addModifyingVariables(statement.getModifyingVariables());
            }
        }
    }
}
