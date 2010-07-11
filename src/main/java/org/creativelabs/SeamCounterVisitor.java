package org.creativelabs;

import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class SeamCounterVisitor extends VoidVisitorAdapter<Object> {

    private Logger logger = Logger.getLogger(this.getClass());

    private Map<String, String> variables;
    private Map<String, String> localVariables;
    private Map<String, Boolean> internalInstances;

    public SeamCounterVisitor(Map<String, String> variables) {
        this.variables = variables;
        localVariables = new HashMap<String, String>();
        internalInstances = new HashMap<String, Boolean>();
    }
    
    public Map<String, String> getLocalVariables() {
        return localVariables;
    }

    public Map<String, Boolean> getAssignedVariables() {
        return internalInstances;
    }

    @Override
    public void visit(BlockStmt n, Object o) {
        List<Statement> stmts = n.getStmts();
        for (Statement statement : stmts) {
            statement.accept(this, o);
        }
    }

    @Override
    public void visit(NameExpr n, Object o) {
        logger.trace(n.getName() + ":" + n.getBeginLine() + " " + n.getBeginColumn());
    }

    @Override
    public void visit(MethodCallExpr n, Object o) {
        ScopeDetectorVisitor scopeDetector = new ScopeDetectorVisitor();
        scopeDetector.visit(n, o);

        logger.debug("Method name " + n.getName() + " Deepest name - " + scopeDetector.getName() + 
                ":" + n.getBeginLine() + " " + n.getBeginColumn());
    }

    @Override
    public void visit(FieldAccessExpr n, Object o) {
        ScopeDetectorVisitor scopeDetector = new ScopeDetectorVisitor();
        scopeDetector.visit(n, o);
        logger.debug("Field name " + n.getField() + " Deepest name - " + scopeDetector.getName() + 
                ":" + n.getBeginLine() + " " + n.getBeginColumn());
        super.visit(n, o);
    }

    @Override
    public void visit(AssignExpr n, Object o) {
        if (n.getValue() != null) {
            logger.trace("Assigning the following expression type " + n.getValue().getClass());
            ExpressionSeparatorVisitor esv = new ExpressionSeparatorVisitor(internalInstances);
            n.getValue().accept(esv, null);
            if (esv.isAssignedInternalInstance()) {
                internalInstances.put(n.getTarget().toString(), true);
                System.out.println(n.getTarget().toString() + " - internal instance");
            }
        }
    }

    @Override
    public void visit(VariableDeclarationExpr n, Object o) {
        for (VariableDeclarator var : n.getVars()) {
            localVariables.put(var.getId().getName(), n.getType().toString());
            logger.trace("Variable declaration name " + var.getId().getName());
            logger.trace("Assigning the following expression " + var.getInit());
            ExpressionSeparatorVisitor esv = new ExpressionSeparatorVisitor(internalInstances);
            if (var.getInit() != null) {
                logger.trace("Assigning the following expression type " + var.getInit().getClass());
                var.getInit().accept(esv, null);
                if (esv.isAssignedInternalInstance()) {
                    internalInstances.put(var.getId().getName(), true);
                    System.out.println(var.getId().getName() + " - internal instance");
                }
            }
        }
    }
}
