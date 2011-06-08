package org.creativelabs.ssa.visitor;

import japa.parser.ast.expr.*;
import japa.parser.ast.visitor.GenericVisitorAdapter;
import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.EmptyCondition;
import org.creativelabs.graph.condition.bool.FalseBooleanCondition;
import org.creativelabs.graph.condition.bool.TrueBooleanCondition;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.helper.GenericVisitorHelper;
import org.creativelabs.ssa.PhiNode;
import org.creativelabs.ssa.holder.MethodModifiersHolder;
import org.creativelabs.ssa.holder.PhiNodesHolder;

import java.util.Set;

import static org.creativelabs.Constants.SEPARATOR;

/**
 * @author azotcsit
 *         Date: 05.05.11
 *         Time: 13:23
 */
public class ConditionFinder extends GenericVisitorAdapter<Condition[], Set<String>> {

    private InternalInstancesGraph graph;
    private String methodName;
    private MethodModifiersHolder modifiersHolder;
    private PhiNodesHolder phiNodesHolder;

    private boolean isMethodArgsProcessing = false;

    public ConditionFinder(InternalInstancesGraph graph, String methodName, MethodModifiersHolder modifiersHolder, PhiNodesHolder phiNodesHolder) {
        this.graph = graph;
        this.methodName = methodName;
        this.modifiersHolder = modifiersHolder;
        this.phiNodesHolder = phiNodesHolder;
    }

    private Condition[] applyAndOperationForConditions(Condition[] conditions1, Condition[] conditions2) {
        return new Condition[]{
                conditions1[0].and(conditions2[0]),
                conditions1[1].and(conditions2[1])};
    }

    private Condition[] applyOrOperationForConditions(Condition[] conditions1, Condition[] conditions2) {
        return new Condition[]{
                conditions1[0].or(conditions2[0]),
                conditions1[1].or(conditions2[1])};
    }

    private Condition[] applyBinaryOperationForConditions(Condition[] conditions1, Condition[] conditions2) {
        return new Condition[]{
                conditions1[0].and(conditions2[0]),
                conditions1[1].or(conditions2[1])};
    }

    @Override
    public Condition[] visit(ObjectCreationExpr n, Set<String> arg) {
        return getDefaultInternalConditions();
    }

    @Override
    public Condition[] visit(ArrayCreationExpr n, Set<String> arg) {
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
    public Condition[] visit(ConditionalExpr n, Set<String> arg) {
        return getDefaultInternalConditions();
    }

    @Override
    public Condition[] visit(BinaryExpr n, Set<String> arg) {
        return applyBinaryOperationForConditions(GenericVisitorHelper.visitExpression(n.getLeft(), arg, this),
                GenericVisitorHelper.visitExpression(n.getRight(), arg, this));
    }

    @Override
    public Condition[] visit(NameExpr n, Set<String> arg) {
        Condition internalCondition = graph.getInternalVertexCondition(methodName + SEPARATOR +
                n.getName());
        Condition externalCondition = graph.getExternalVertexCondition(methodName + SEPARATOR +
                n.getName());

        if (!isMethodArgsProcessing) {
            arg.add(n.getName());
        }

        if (!(internalCondition instanceof EmptyCondition)
                && !(externalCondition instanceof EmptyCondition)) {
            return new Condition[]{internalCondition.<Condition>copy(), externalCondition.<Condition>copy()};
        }

        for (PhiNode phiNode : phiNodesHolder.getPhiNodes()) {
//            TODO bla bla
//            if (phiNode.getName().indexOf(SEPARATOR) == -1) {
                if (phiNode.getName().equals(n.getName().substring(0, n.getName().indexOf(SEPARATOR)))) {
                    Condition[] condition = new Condition[]{new EmptyCondition(), new EmptyCondition()};
                    for (Integer index : phiNode.getIndexes()) {
                        condition = applyOrOperationForConditions(
                                condition,
                                new Condition[]{
                                        graph.getInternalVertexCondition(methodName + SEPARATOR + phiNode.getName() + SEPARATOR + index),
                                        graph.getExternalVertexCondition(methodName + SEPARATOR + phiNode.getName() + SEPARATOR + index)});
                        graph.addEdge(
                                methodName + SEPARATOR + phiNode.getName() + SEPARATOR + phiNode.getLeftIndex(),
                                methodName + SEPARATOR + phiNode.getName() + SEPARATOR + index);
                    }
                    graph.addVertexConditions(
                            methodName + SEPARATOR + phiNode.getName() + SEPARATOR + phiNode.getLeftIndex(),
                            condition[0],
                            condition[1]);
                    if (n.getName().equals(phiNode.getName() + SEPARATOR + phiNode.getLeftIndex())) {
                        return condition;
                    }
                }
//            }
        }

        throw new IllegalStateException("Variable " + n.getName() + " is not found in graph. " +
                "Conditions couldn't be processed.");
    }

    @Override
    public Condition[] visit(FieldAccessExpr n, Set<String> arg) {
        if (n.getScope() instanceof ThisExpr) {
            Condition internalCondition = graph.getInternalVertexCondition(methodName + SEPARATOR +
                    n.getField());
            Condition externalCondition = graph.getExternalVertexCondition(methodName + SEPARATOR +
                    n.getField());

            if (!isMethodArgsProcessing) {
                arg.add(n.getField());
            }

            if (!(internalCondition instanceof EmptyCondition)
                    && !(externalCondition instanceof EmptyCondition)) {
                return new Condition[]{internalCondition.<Condition>copy(), externalCondition.<Condition>copy()};
            }

            for (PhiNode phiNode : phiNodesHolder.getPhiNodes()) {
                if (phiNode.getName().equals(n.getField().substring(0, n.getField().indexOf(SEPARATOR)))) {
                    Condition[] condition = new Condition[]{new EmptyCondition(), new EmptyCondition()};
                    for (Integer index : phiNode.getIndexes()) {
                        condition = applyOrOperationForConditions(
                                condition,
                                new Condition[]{
                                        graph.getInternalVertexCondition(methodName + SEPARATOR + phiNode.getName() + SEPARATOR + index),
                                        graph.getExternalVertexCondition(methodName + SEPARATOR + phiNode.getName() + SEPARATOR + index)});
                        graph.addEdge(
                                methodName + SEPARATOR + phiNode.getName() + SEPARATOR + phiNode.getLeftIndex(),
                                methodName + SEPARATOR + phiNode.getName() + SEPARATOR + index);
                    }
                    graph.addVertexConditions(
                            methodName + SEPARATOR + phiNode.getName() + SEPARATOR + phiNode.getLeftIndex(),
                            condition[0],
                            condition[1]);
                    if (n.getField().equals(phiNode.getName() + SEPARATOR + phiNode.getLeftIndex())) {
                        return condition;
                    }
                }
            }
        } else {
//            return GenericVisitorHelper.visitExpression(n.getScope(), arg, this);
            //TODO to implement support of inner fields
            return getDefaultInternalConditions();
        }

        throw new IllegalStateException("Variable " + n.getField() + " is not found in graph. " +
                "Conditions couldn't be processed.");
    }

    @Override
    public Condition[] visit(MethodCallExpr n, Set<String> arg) {
        isMethodArgsProcessing = true;
        //TODO to implement!!!!
        arg.add(n.getName());
        isMethodArgsProcessing = false;
//        modifiersHolder.
        return getDefaultInternalConditions();
    }

    @Override
    public Condition[] visit(UnaryExpr n, Set<String> arg) {
        return GenericVisitorHelper.visitExpression(n.getExpr(), arg, this);
    }

    @Override
    public Condition[] visit(InstanceOfExpr n, Set<String> arg) {
        return GenericVisitorHelper.visitExpression(n.getExpr(), arg, this);
    }

    @Override
    public Condition[] visit(CastExpr n, Set<String> arg) {
        return GenericVisitorHelper.visitExpression(n.getExpr(), arg, this);
    }

    @Override
    public Condition[] visit(EnclosedExpr n, Set<String> arg) {
        return GenericVisitorHelper.visitExpression(n.getInner(), arg, this);
    }

    private static Condition[] getDefaultInternalConditions() {
        return new Condition[]{new TrueBooleanCondition(), new FalseBooleanCondition()};
    }

    private static Condition[] getDefaultExternalConditions() {
        return new Condition[]{new FalseBooleanCondition(), new TrueBooleanCondition()};
    }
}
