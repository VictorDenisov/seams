package org.creativelabs;

import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpressionSeparatorVisitor extends VoidVisitorAdapter<Object> {

    private boolean assignedInternalInstance = false;

    Map<String, Boolean> internalInstances;

    public ExpressionSeparatorVisitor(Map<String, Boolean> internalInstances) {
        this.internalInstances = internalInstances;
    }

    public boolean isAssignedInternalInstance() {
        return assignedInternalInstance;
    }

    @Override
    public void visit(NameExpr n, Object o) {
        String name = n.getName();
        if (internalInstances.containsKey(name)) {
            assignedInternalInstance = true;
        }
    }

    @Override
    public void visit(FieldAccessExpr n, Object o) {
        ScopeDetectorVisitor scopeDetector = new ScopeDetectorVisitor();
        scopeDetector.visit(n, o);
        String name = scopeDetector.getName();
        if (internalInstances.containsKey(name)) {
            assignedInternalInstance = true;
        }
    }

    @Override
    public void visit(MethodCallExpr n, Object o) {
        ScopeDetectorVisitor scopeDetector = new ScopeDetectorVisitor();
        scopeDetector.visit(n, o);
        String name = scopeDetector.getName();
        if (internalInstances.containsKey(name)) {
            assignedInternalInstance = true;
        }
    }

    @Override
    public void visit(ExplicitConstructorInvocationStmt n, Object o) {
        System.out.println("Processing constructor invocation");
    }

    @Override
    public void visit(ObjectCreationExpr n, Object o) {
        assignedInternalInstance = true;
        System.out.println("Construction of " + n.getType().getName());
    }

}
