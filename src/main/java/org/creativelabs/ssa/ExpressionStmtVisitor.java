package org.creativelabs.ssa;

import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.*;
import japa.parser.ast.visitor.GenericVisitorAdapter;
import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.bool.FalseBooleanCondition;
import org.creativelabs.graph.condition.bool.TrueBooleanCondition;
import org.creativelabs.iig.InternalInstancesGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author azotcsit
 *         Date: 09.05.11
 *         Time: 15:10
 */
public class ExpressionStmtVisitor extends GenericVisitorAdapter<Expression, VariablesHolder> {

    private static final String UNSUPPORTED = "Unsupported expression: ";
    private static final String NULL = "Expression is null ";
    private static final String SEPARATOR = "#";
    private static final String NOT_CONTAINS = "<not contains in key set>";

    private boolean isNeededToIncreaseIndex;

    private InternalInstancesGraph graph;

    private String methodName;

    public ExpressionStmtVisitor(InternalInstancesGraph graph, String methodName) {
        this.graph = graph;
        this.methodName = methodName;
    }

    @Override
    public Expression visit(NameExpr n, VariablesHolder arg) {
        String variableName = n.getName();

        boolean isClassWithStaticMethod = Character.isUpperCase(variableName.charAt(0));
        if (isClassWithStaticMethod) {
            return n;
        }

        Integer variableIndex = arg.read(variableName);
        if (variableIndex != null) {
            if (isNeededToIncreaseIndex) {
                variableIndex = arg.readFrom(variableName, false);
                variableIndex++;
                arg.write(variableName, variableIndex);
            }
            n.setName(variableName + SEPARATOR + variableIndex);
        } else {
            n.setName(variableName + " " + NOT_CONTAINS);
        }

        if (arg.containField(variableName)) {
            return new FieldAccessExpr(new ThisExpr(), n.getName());
        }

        return n;
    }

    @Override
    public Expression visit(BinaryExpr n, VariablesHolder arg) {
        n.setRight(GenericVisitorHelper.visitExpression(n.getRight(), arg, this));
        n.setLeft(GenericVisitorHelper.visitExpression(n.getLeft(), arg, this));
        return n;
    }

    @Override
    public Expression visit(ArrayAccessExpr n, VariablesHolder arg) {
        boolean b = isNeededToIncreaseIndex;
        isNeededToIncreaseIndex = false;

        n.setIndex(GenericVisitorHelper.visitExpression(n.getIndex(), arg, this));
        n.setName(GenericVisitorHelper.visitExpression(n.getName(), arg, this));

        isNeededToIncreaseIndex = b;

        List<Expression> args = new ArrayList<Expression>();
        args.add(n.getName());
        args.add(n.getIndex());

        return new MethodCallExpr(null, "#Access", args);
    }

    @Override
    public Expression visit(ArrayCreationExpr n, VariablesHolder arg) {
        if (n.getInitializer() != null) {
            n.setInitializer((ArrayInitializerExpr) GenericVisitorHelper.visitExpression(n.getInitializer(), arg, this));
        }
        if (n.getDimensions() != null) {
            List<Expression> expressions = new ArrayList<Expression>();
            for (Expression expression : n.getDimensions()) {
                expressions.add(GenericVisitorHelper.visitExpression(expression, arg, this));
            }
            n.setDimensions(expressions);
        }
        return n;
    }

    @Override
    public Expression visit(ArrayInitializerExpr expr, VariablesHolder arg) {
        if (expr.getValues() != null) {
            List<Expression> expressions = new ArrayList<Expression>();
            for (Expression expression : expr.getValues()) {
                expressions.add(GenericVisitorHelper.visitExpression(expression, arg, this));
            }
            expr.setValues(expressions);
        }
        return expr;
    }

    @Override
    public Expression visit(ObjectCreationExpr n, VariablesHolder arg) {
        if (n.getArgs() != null) {
            List<Expression> expressions = new ArrayList<Expression>();
            for (Expression expression : n.getArgs()) {
                expressions.add(GenericVisitorHelper.visitExpression(expression, arg, this));
            }
            n.setArgs(expressions);
        }
        return n;
    }

    @Override
    public Expression visit(MethodCallExpr n, VariablesHolder arg) {
        if (n.getArgs() != null) {
            List<Expression> expressions = new ArrayList<Expression>();
            for (Expression expression : n.getArgs()) {
                expressions.add(GenericVisitorHelper.visitExpression(expression, arg, this));
            }
            n.setArgs(expressions);
        }
        if (n.getScope() != null) {
            n.setScope(GenericVisitorHelper.visitExpression(n.getScope(), arg, this));
        }
        return n;
    }

    @Override
    public Expression visit(InstanceOfExpr n, VariablesHolder arg) {
        if (n.getExpr() != null) {
            n.setExpr(GenericVisitorHelper.visitExpression(n.getExpr(), arg, this));
        }
        return n;
    }

    @Override
    public Expression visit(CastExpr n, VariablesHolder arg) {
        if (n.getExpr() != null) {
            n.setExpr(GenericVisitorHelper.visitExpression(n.getExpr(), arg, this));
        }
        return n;
    }

    @Override
    public Expression visit(EnclosedExpr n, VariablesHolder arg) {
        if (n.getInner() != null) {
            n.setInner(GenericVisitorHelper.visitExpression(n.getInner(), arg, this));
        }
        return n;
    }

    @Override
    public Expression visit(UnaryExpr n, VariablesHolder arg) {
        if (n.getExpr() != null) {
            n.setExpr(GenericVisitorHelper.visitExpression(n.getExpr(), arg, this));
        }
        return n;
    }

    @Override
    public Expression visit(ThisExpr n, VariablesHolder arg) {
        return n;
    }

    @Override
    public Expression visit(SuperExpr n, VariablesHolder arg) {
        return n;
    }

    @Override
    public Expression visit(VariableDeclarationExpr n, VariablesHolder arg) {
        List<VariableDeclarator> vars = n.getVars();
        for (VariableDeclarator variableDeclarator : vars) {
            String variableName = variableDeclarator.getId().getName();
            arg.write(variableName, 0);
            if (variableDeclarator.getInit() != null) {
                variableDeclarator.setInit(GenericVisitorHelper.visitExpression(variableDeclarator.getInit(), arg, this));
            }
            variableDeclarator.getId().setName(variableName + SEPARATOR + 0);

            String target = methodName + SEPARATOR + variableName;

            Condition[] conditions = null;
            if (variableDeclarator.getInit() != null) {
                Set<String> valuesNames = new TreeSet<String>();
                conditions = GenericVisitorHelper.visitExpression(
                        variableDeclarator.getInit(),
                        valuesNames,
                        new ConditionFinder(graph, methodName));
                for (String name : valuesNames) {
                    graph.addEdge(target + SEPARATOR + 0, name);

                }
            } else {
                conditions = new Condition[]{new TrueBooleanCondition(), new FalseBooleanCondition()};
            }

            graph.addVertexConditions(target + SEPARATOR + 0,
                    conditions[0].and(arg.getCondition()),
                    conditions[1].and(arg.getCondition()));
        }

        n.setVars(vars);
        return n;
    }

    @Override
    public Expression visit(AssignExpr n, VariablesHolder arg) {
        if (n.getTarget() instanceof ArrayAccessExpr) {

            String arrayName = null;
            if (((ArrayAccessExpr) n.getTarget()).getName() instanceof FieldAccessExpr) {
                arrayName = ((FieldAccessExpr) ((ArrayAccessExpr) n.getTarget()).getName()).getField();
            } else {
                arrayName = ((NameExpr) ((ArrayAccessExpr) n.getTarget()).getName()).getName();
            }

            ((ArrayAccessExpr) n.getTarget()).setIndex(GenericVisitorHelper.visitExpression(
                    ((ArrayAccessExpr) n.getTarget()).getIndex(), arg, this));
            n.setValue(GenericVisitorHelper.visitExpression(n.getValue(), arg, this));

            String index = ((ArrayAccessExpr) n.getTarget()).getIndex().toString();

            List<Expression> expressions = new ArrayList<Expression>();
            expressions.add(new NameExpr(arrayName + SEPARATOR + arg.read(arrayName)));
            expressions.add(new NameExpr(index));
            expressions.add(n.getValue());

            n.setValue(new MethodCallExpr(null, "Update", expressions));

            arg.increaseIndex(arrayName);
            n.setTarget(new NameExpr(arrayName + SEPARATOR + arg.read(arrayName)));

            String value = methodName + SEPARATOR + arrayName + SEPARATOR + arg.read(arrayName);
            String target = methodName + SEPARATOR + expressions.get(2).toString();

//            Condition[] conditions = GenericVisitorHelper.visitExpression(
//                    expressions.get(2),
//                    null,
//                    new ConditionFinder(graph, methodName));

//            graph.addVertexConditions(target,
//                    conditions[0].or(graph.getInternalVertexCondition(target)),
//                    conditions[1].or(graph.getExternalVertexCondition(target)));
//
//            graph.addEdge(methodName + SEPARATOR + arrayName + SEPARATOR + arg.read(arrayName),
//                    methodName + SEPARATOR + expressions.get(2).toString());
        } else if (n.getTarget() instanceof NameExpr) {
            n.setValue(GenericVisitorHelper.<Expression, VariablesHolder>visitExpression(n.getValue(), arg, this));
            String variableName = n.getTarget().toString();

            isNeededToIncreaseIndex = true;
            n.setTarget(GenericVisitorHelper.<Expression, VariablesHolder>visitExpression(n.getTarget(), arg, this));
            isNeededToIncreaseIndex = false;

            String target = methodName + SEPARATOR + variableName;

            Set<String> valuesNames = new TreeSet<String>();
            Condition[] conditions = GenericVisitorHelper.visitExpression(
                    n.getValue(),
                    valuesNames,
                    new ConditionFinder(graph, methodName));
            for (String name : valuesNames) {
                graph.addEdge(target + SEPARATOR + arg.read(variableName), name);

            }


            graph.addVertexConditions(target + SEPARATOR + arg.read(variableName),
                    conditions[0].and(arg.getCondition()),
                    conditions[1].and(arg.getCondition()));


//            Condition[] conditions = GenericVisitorHelper.visitExpression(
//                    n.getValue(),
//                    null,
//                    new ConditionFinder(graph, methodName));

//            graph.addVertexConditions(target,
//                    conditions[0].or(graph.getInternalVertexCondition(target)),
//                    conditions[1].or(graph.getExternalVertexCondition(target)));
//
//            graph.addVertexConditions(target,
//                    graph.getInternalVertexCondition(target).or(graph.getInternalVertexCondition(value)),
//                    graph.getExternalVertexCondition(target).or(graph.getExternalVertexCondition(value)));
//
//            graph.addEdge(methodName + SEPARATOR + n.getTarget().toString(),
//                    methodName + SEPARATOR + n.getValue().toString());
        } else {
            throw new IllegalStateException("Class " + n.getTarget().getClass() + " is not supported " +
                    "by ExpressionStmtVisitor as assign target expression.");
        }

        return n;
    }


    @Override
    public Expression visit(FieldAccessExpr n, VariablesHolder arg) {

        String name = n.getField();
        if (arg.read(name) != null) {
            if (isNeededToIncreaseIndex) {
                arg.increaseIndex(name);
                n.setField(name + SEPARATOR + arg.read(name));
            } else {
                n.setField(name + SEPARATOR + arg.read(name));
            }
        }

        n.setScope(GenericVisitorHelper.visitExpression(n.getScope(), arg, this));
        return n;
    }

    @Override
    public Expression visit(ConditionalExpr n, VariablesHolder arg) {
        n.setCondition(GenericVisitorHelper.visitExpression(n.getCondition(), arg, this));
        n.setThenExpr(GenericVisitorHelper.visitExpression(n.getThenExpr(), arg, this));
        n.setElseExpr(GenericVisitorHelper.visitExpression(n.getElseExpr(), arg, this));
        return n;
    }

    @Override
    public Expression visit(IntegerLiteralExpr n, VariablesHolder arg) {
        return n;
    }

    @Override
    public Expression visit(IntegerLiteralMinValueExpr n, VariablesHolder arg) {
        return n;
    }

    @Override
    public Expression visit(LongLiteralExpr n, VariablesHolder arg) {
        return n;
    }

    @Override
    public Expression visit(LongLiteralMinValueExpr n, VariablesHolder arg) {
        return n;
    }

    @Override
    public Expression visit(CharLiteralExpr n, VariablesHolder arg) {
        return n;
    }

    @Override
    public Expression visit(BooleanLiteralExpr n, VariablesHolder arg) {
        return n;
    }

    @Override
    public Expression visit(DoubleLiteralExpr n, VariablesHolder arg) {
        return n;
    }

    @Override
    public Expression visit(StringLiteralExpr n, VariablesHolder arg) {
        return n;
    }

    @Override
    public Expression visit(NullLiteralExpr n, VariablesHolder arg) {
        return n;
    }
}
