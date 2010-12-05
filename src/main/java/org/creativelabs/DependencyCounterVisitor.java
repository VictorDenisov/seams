package org.creativelabs;

import japa.parser.ast.stmt.*;
import japa.parser.ast.expr.*;
import japa.parser.ast.body.*;
import japa.parser.ast.visitor.*;
import java.util.*;

class DependencyCounterVisitor extends VoidVisitorAdapter<Object> {
    private FieldList classFields;

    private ImportList imports;

    DependencyCounterVisitor(FieldList classFields, ImportList imports) {
        this.classFields = classFields;
        this.imports = imports;
    }

    private Set<Dependency> dependencies = new HashSet<Dependency>();

    private Map<String, String> localVariables = new HashMap<String, String>();

    private Map<String, Boolean> internalInstances = new HashMap<String, Boolean>();

    Set<Dependency> getDependencies() {
        return Collections.unmodifiableSet(dependencies);
    }

    @Override
    public void visit(BlockStmt n, Object o) {
        for (Statement statement : n.getStmts()) {
            statement.accept(this, o);
        }
    }

    @Override
    public void visit(NameExpr n, Object o) {
        String dependencyUponType = null;
        if (localVariables.containsKey(n.getName())) {
            dependencyUponType = localVariables.get(n.getName());
        } else if (classFields.hasName(n.getName())) {
            dependencyUponType = classFields.getFieldTypeAsString(n.getName());
        } else if (Character.isUpperCase(n.getName().charAt(0))) {
            dependencyUponType = n.getName();
        }
        dependencies.add(new Dependency(n.getName(), dependencyUponType));
    }
    
    @Override
    public void visit(MethodCallExpr n, Object o) {
        ScopeDetectorVisitor scopeDetector = new ScopeDetectorVisitor();
        scopeDetector.visit(n, o);
        dependencies.add(new Dependency(n.toString(), null));
        super.visit(n, o);
    }

    @Override
    public void visit(FieldAccessExpr n, Object o) {
        ScopeDetectorVisitor scopeDetector = new ScopeDetectorVisitor();
        scopeDetector.visit(n, o);
        dependencies.add(new Dependency(n.toString(), null));
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

