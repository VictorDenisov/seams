package org.creativelabs;

import japa.parser.ast.stmt.*;
import japa.parser.ast.expr.*;
import japa.parser.ast.body.*;
import japa.parser.ast.visitor.*;
import java.util.*;

class DependencyCounterVisitor extends VoidVisitorAdapter<Object> {
    private Map<String, String> classFields;

    DependencyCounterVisitor(Map<String, String> classFields) {
        this.classFields = classFields;
    }

    private Set<String> dependencies = new HashSet<String>();

    private Set<String> dependenciesUponType = new HashSet<String>();

    private Map<String, String> localVariables = new HashMap<String, String>();

    private Map<String, Boolean> internalInstances = new HashMap<String, Boolean>();

    Set<String> getDependencies() {
        return Collections.unmodifiableSet(dependencies);
    }

    Set<String> getDependenciesUponType() {
        return Collections.unmodifiableSet(dependenciesUponType);
    }

    @Override
    public void visit(BlockStmt n, Object o) {
        for (Statement statement : n.getStmts()) {
            statement.accept(this, o);
        }
    }

    @Override
    public void visit(NameExpr n, Object o) {
        dependencies.add(n.getName());
        if (localVariables.containsKey(n.getName())) {
            dependenciesUponType.add(localVariables.get(n.getName()));
        }
        if (classFields.containsKey(n.getName())) {
            dependenciesUponType.add(classFields.get(n.getName()));
        }
        if (Character.isUpperCase(n.getName().charAt(0))) {
            dependenciesUponType.add(n.getName());
        }
    }
    
    @Override
    public void visit(MethodCallExpr n, Object o) {
        ScopeDetectorVisitor scopeDetector = new ScopeDetectorVisitor();
        scopeDetector.visit(n, o);
        dependencies.add(n.toString());
        super.visit(n, o);
    }

    @Override
    public void visit(FieldAccessExpr n, Object o) {
        ScopeDetectorVisitor scopeDetector = new ScopeDetectorVisitor();
        scopeDetector.visit(n, o);
        dependencies.add(n.toString());
        super.visit(n, o);
    }

    @Override
    public void visit(AssignExpr n, Object o) {
        if (n.getValue() != null) {
            ExpressionSeparatorVisitor esv = new ExpressionSeparatorVisitor(internalInstances);
            n.getValue().accept(esv, null);
            if (esv.isAssignedInternalInstance()) {
                internalInstances.put(n.getTarget().toString(), true);
            }
        }
        super.visit(n, o);
    }

    @Override
    public void visit(VariableDeclarationExpr n, Object o) {
        for (VariableDeclarator v : n.getVars()) {
            localVariables.put(v.getId().getName(), n.getType().toString());
            ExpressionSeparatorVisitor esv = new ExpressionSeparatorVisitor(internalInstances);
            if (v.getInit() != null) {
                v.getInit().accept(esv, null);
                if (esv.isAssignedInternalInstance()) {
                    internalInstances.put(v.getId().getName(), true);
                }
            }
        }
        super.visit(n, o);
    }
}


// vim: set ts=4 sw=4 et:

