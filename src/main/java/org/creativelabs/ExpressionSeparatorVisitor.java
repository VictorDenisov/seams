package org.creativelabs;

import japa.parser.ast.body.*;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.util.*;

public class ExpressionSeparatorVisitor extends VoidVisitorAdapter<Object> {

    private boolean assignedInternalInstance = false;

    private Map<String, Boolean> internalInstances;

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
        //TODO Redirect to file
        System.out.println("Processing constructor invocation");
    }

    @Override
    public void visit(ObjectCreationExpr n, Object o) {
        assignedInternalInstance = true;
        //TODO Redirect to file
        System.out.println("Construction of " + n.getType().getName());
    }

}
