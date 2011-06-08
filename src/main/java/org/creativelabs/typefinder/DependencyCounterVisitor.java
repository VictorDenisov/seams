package org.creativelabs.typefinder;

import japa.parser.ast.stmt.*;
import japa.parser.ast.expr.*;
import japa.parser.ast.body.*;
import japa.parser.ast.visitor.*;

import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.iig.SimpleInternalInstancesGraph;
import org.creativelabs.introspection.*;

import java.util.*;

public class DependencyCounterVisitor extends VoidVisitorAdapter<Object> {
    protected VariableList classFields;

    private ImportList imports;

    public DependencyCounterVisitor(VariableList classFields, ImportList imports) {
        this.classFields = classFields;
        this.imports = imports;
    }

    private Set<Dependency> dependencies = new HashSet<Dependency>();

    protected VariableList localVariables = new VariableListBuilder().buildEmpty();

    private InternalInstancesGraph internalInstances = new SimpleInternalInstancesGraph();

    public Set<Dependency> getDependencies() {
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
        super.visit(n, o);
    }

    @Override
    public void visit(NameExpr n, Object o) {
        ClassType dependencyUponType = null;
        dependencyUponType = runTypeFinder(n);
        dependencies.add(new Dependency(n.getName(), dependencyUponType));
    }

    protected ClassType runTypeFinder(Expression n) {
        VariableList vList = new VariableListBuilder().buildEmpty();
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
    }

    @Override
    public void visit(AssignExpr n, Object o) {
        if (n.getValue() != null) {
            ExpressionSeparatorVisitor esv = new ExpressionSeparatorVisitor(internalInstances);
            n.getValue().accept(esv, null);
            if (esv.isAssignedInternalInstance()) {
                internalInstances.addEdge(n.getTarget().toString(), esv.getValue());
            }
        }
        super.visit(n, o);
    }

    @Override
    public void visit(VariableDeclarationExpr n, Object o) {
        for (VariableDeclarator v : n.getVars()) {
            ClassType classType = imports.getClassByType(n.getType());
            classType = ReflectionAbstractionImpl.create()
                .convertToArray(classType, v.getId().getArrayCount());

            localVariables.put(v.getId().getName(), classType);
            ExpressionSeparatorVisitor esv = new ExpressionSeparatorVisitor(internalInstances);
            if (v.getInit() != null) {
                v.getInit().accept(esv, null);
                if (esv.isAssignedInternalInstance()) {
                    internalInstances.addEdge(v.getId().getName(), esv.getValue());
                }
            }
        }
        super.visit(n, o);
    }

    @Override
    public void visit(Parameter n, Object o) {
        ClassType classType = imports.getClassByType(n.getType());

        localVariables.put(n.getId().getName(), classType);
    }
}

// vim: set ts=4 sw=4 et:
