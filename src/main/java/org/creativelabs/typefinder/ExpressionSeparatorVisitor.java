package org.creativelabs.typefinder;

import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import org.creativelabs.iig.InternalInstancesGraph;

import java.util.Set;

public class ExpressionSeparatorVisitor extends VoidVisitorAdapter<Object> {

    private boolean assignedInternalInstance = false;
    
    private String value = null;

    private InternalInstancesGraph internalInstances;

    private Set<String> excludedClasses;

    public ExpressionSeparatorVisitor(InternalInstancesGraph internalInstances,
            Set<String> excludedClasses) {
        this.internalInstances = internalInstances;
        this.excludedClasses = excludedClasses;
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
        if (excludedClasses.contains(n.getType().getName())) {
            return;
        }
        assignedInternalInstance = true;
    }

}

