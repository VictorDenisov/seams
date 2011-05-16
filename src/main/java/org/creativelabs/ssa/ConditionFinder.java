package org.creativelabs.ssa;

import japa.parser.ast.expr.*;
import japa.parser.ast.visitor.GenericVisitorAdapter;
import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.EmptyCondition;
import org.creativelabs.graph.condition.bool.FalseBooleanCondition;
import org.creativelabs.graph.condition.bool.TrueBooleanCondition;
import org.creativelabs.iig.InternalInstancesGraph;

import java.util.Set;

import static org.creativelabs.ssa.SsaFormConverter.SEPARATOR;

/**
 * @author azotcsit
 *         Date: 05.05.11
 *         Time: 13:23
 */
public class ConditionFinder extends GenericVisitorAdapter<Condition[], Set<String>> {

    private InternalInstancesGraph graph;
    private String methodName;
    private boolean isMethodArgsProcessing = false;

    public ConditionFinder(InternalInstancesGraph graph, String methodName) {
        this.graph = graph;
        this.methodName = methodName;
    }

    private Condition[] applyAndOperationForConditions(Condition[] conditions1, Condition[] conditions2) {
        conditions1[0].and(conditions2[0]);
        conditions1[1].and(conditions2[1]);
        return conditions1;
    }

    private Condition[] applyOrOperationForConditions(Condition[] conditions1, Condition[] conditions2) {
        conditions1[0].or(conditions2[0]);
        conditions1[1].or(conditions2[1]);
        return conditions1;
    }

    @Override
    public Condition[] visit(ObjectCreationExpr n, Set<String> arg) {
        return getDefaultInternalConditions();
    }

    @Override
    public Condition[] visit(IntegerLiteralExpr n, Set<String> arg) {
        return getDefaultInternalConditions();
    }

    @Override
    public Condition[] visit(IntegerLiteralMinValueExpr n, Set<String> arg) {
        return getDefaultInternalConditions();
    }

    @Override
    public Condition[] visit(LongLiteralExpr n, Set<String> arg) {
        return getDefaultInternalConditions();
    }

    @Override
    public Condition[] visit(LongLiteralMinValueExpr n, Set<String> arg) {
        return getDefaultInternalConditions();
    }

    @Override
    public Condition[] visit(DoubleLiteralExpr n, Set<String> arg) {
        return getDefaultInternalConditions();
    }

    @Override
    public Condition[] visit(BooleanLiteralExpr n, Set<String> arg) {
        return getDefaultInternalConditions();
    }

    @Override
    public Condition[] visit(CharLiteralExpr n, Set<String> arg) {
        return getDefaultInternalConditions();
    }

    @Override
    public Condition[] visit(StringLiteralExpr n, Set<String> arg) {
        return getDefaultInternalConditions();
    }

    @Override
    public Condition[] visit(NullLiteralExpr n, Set<String> arg) {
        return getDefaultInternalConditions();
    }

    @Override
    public Condition[] visit(BinaryExpr n, Set<String> arg) {
        return applyOrOperationForConditions(GenericVisitorHelper.visitExpression(n.getLeft(), arg, this),
                GenericVisitorHelper.visitExpression(n.getRight(), arg, this));
    }

    @Override
    public Condition[] visit(NameExpr n, Set<String> arg) {
        Condition internalCondition = graph.getInternalVertexCondition(methodName + SEPARATOR +
                n.getName()/*.substring(0, n.getName().lastIndexOf(SEPARATOR))*/);
        Condition externalCondition = graph.getExternalVertexCondition(methodName + SEPARATOR +
                n.getName()/*.substring(0, n.getName().lastIndexOf(SEPARATOR))*/);

        if (!isMethodArgsProcessing) {
            arg.add(methodName + SEPARATOR + n.getName());
        }

        if (!(internalCondition instanceof EmptyCondition)
                && !(externalCondition instanceof EmptyCondition)) {
            return new Condition[] {internalCondition.copy(), externalCondition.copy()};
        }

        throw new IllegalStateException("Variable " + n.getName() + " is not found in graph. " +
                "Conditions couldn't be processed.");
    }

    @Override
    public Condition[] visit(MethodCallExpr n, Set<String> arg) {
        isMethodArgsProcessing = true;
        //TODO to implement!!!!
        arg.add(n.getName());
        isMethodArgsProcessing = false;
        return getDefaultInternalConditions();
    }

    @Override
    public Condition[] visit(InstanceOfExpr n, Set<String> arg) {
        return GenericVisitorHelper.visitExpression(n.getExpr(), arg, this);
    }

    @Override
    public Condition[] visit(CastExpr n, Set<String> arg) {
        return GenericVisitorHelper.visitExpression(n.getExpr(), arg, this);
    }

    private static Condition[] getDefaultInternalConditions() {
            return  new Condition[] {new TrueBooleanCondition(), new FalseBooleanCondition()};
    }

    private static Condition[] getDefaultExternalConditions() {
            return  new Condition[] {new FalseBooleanCondition(), new TrueBooleanCondition()};
    }
}
