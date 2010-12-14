package org.creativelabs;

import japa.parser.ast.body.*;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.util.*;

public class ExpressionSeparatorVisitor extends VoidVisitorAdapter<Object> {

    private boolean assignedInternalInstance = false;
    
    private String value = null;

    private InternalInstancesGraph internalInstances;

    public ExpressionSeparatorVisitor(InternalInstancesGraph internalInstances) {
        this.internalInstances = internalInstances;
    }

    public boolean isAssignedInternalInstance() {
        return assignedInternalInstance;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void visit(NameExpr n, Object o) {
        String name = n.getName();
        if (internalInstances.contains(name)) {
            assignedInternalInstance = true;
            value = name;
        }
    }

    @Override
    public void visit(FieldAccessExpr n, Object o) {
        ScopeDetectorVisitor scopeDetector = new ScopeDetectorVisitor();
        scopeDetector.visit(n, o);
        String name = scopeDetector.getName();
        if (internalInstances.contains(name)) {
            assignedInternalInstance = true;
            value = name;
        }
    }

    @Override
    public void visit(MethodCallExpr n, Object o) {
        ScopeDetectorVisitor scopeDetector = new ScopeDetectorVisitor();
        scopeDetector.visit(n, o);
        String name = scopeDetector.getName();
        if (internalInstances.contains(name)) {
            assignedInternalInstance = true;
            value = name;
        }
    }

    @Override
    public void visit(ObjectCreationExpr n, Object o) {
        assignedInternalInstance = true;
    }

}
