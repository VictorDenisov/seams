package org.creativelabs;

import japa.parser.ast.stmt.*;
import japa.parser.ast.expr.*;
import japa.parser.ast.body.*;
import japa.parser.ast.visitor.*;

import org.creativelabs.introspection.*;

import java.util.*;

class DependencyCounterVisitor extends VoidVisitorAdapter<Object> {
    private VariableList classFields;

    private ImportList imports;

    DependencyCounterVisitor(VariableList classFields, ImportList imports) {
        this.classFields = classFields;
        this.imports = imports;
    }

    private Set<Dependency> dependencies = new HashSet<Dependency>();

    private VariableList localVariables = new VariableList();

    private InternalInstancesGraph internalInstances = new InternalInstancesGraph();

    public void cleanUp() {
        dependencies = new HashSet<Dependency>();
        internalInstances = new InternalInstancesGraph();
        localVariables = new VariableList();
    }

    Set<Dependency> getDependencies() {
        return Collections.unmodifiableSet(dependencies);
    }

    public InternalInstancesGraph getInternalInstances() {
        return internalInstances;
    }

    @Override
    public void visit(BlockStmt n, Object o) {
        if (n == null) {
            return;
        }
        if (n.getStmts() != null) {
            for (Statement statement : n.getStmts()) {
                statement.accept(this, o);
            }
        }
    }

    @Override
    public void visit(NameExpr n, Object o) {
        ClassType dependencyUponType = null;
        if (localVariables.hasName(n.getName())) {
            dependencyUponType = localVariables.getFieldTypeAsClass(n.getName());
        } else if (classFields.hasName(n.getName())) {
            dependencyUponType = classFields.getFieldTypeAsClass(n.getName());
        } else if (Character.isUpperCase(n.getName().charAt(0))) {
            dependencyUponType = imports.getClassByShortName(n.getName());
        }
        dependencies.add(new Dependency(n.getName(), dependencyUponType));
    }

    private ClassType runTypeFinder(Expression n) {
        VariableList vList = new VariableList();
        vList.addAll(classFields);
        vList.addAll(localVariables);
        ClassType type = null;
        type = new TypeFinder(vList, imports).determineType(n);
        return type;
    }

    @Override
    public void visit(MethodCallExpr n, Object o) {
        ClassType type = runTypeFinder(n);

        dependencies.add(new Dependency(n.toString(), type));
        super.visit(n, o);
    }

    @Override
    public void visit(FieldAccessExpr n, Object o) {
        ClassType type = runTypeFinder(n);

        dependencies.add(new Dependency(n.toString(), type));
        super.visit(n, o);
    }

    @Override
    public void visit(AssignExpr n, Object o) {
        if (n.getValue() != null) {
            ExpressionSeparatorVisitor esv = new ExpressionSeparatorVisitor(internalInstances);
            n.getValue().accept(esv, null);
            if (esv.isAssignedInternalInstance()) {
                internalInstances.add(n.getTarget().toString(), esv.getValue());
            }
        }
        super.visit(n, o);
    }

    @Override
    public void visit(VariableDeclarationExpr n, Object o) {
        for (VariableDeclarator v : n.getVars()) {
            ClassType classType = imports.getClassByShortName(n.getType().toString());

            localVariables.put(v.getId().getName(), classType);
            ExpressionSeparatorVisitor esv = new ExpressionSeparatorVisitor(internalInstances);
            if (v.getInit() != null) {
                v.getInit().accept(esv, null);
                if (esv.isAssignedInternalInstance()) {
                    internalInstances.add(v.getId().getName(), esv.getValue());
                }
            }
        }
        super.visit(n, o);
    }
}

// vim: set ts=4 sw=4 et:

