package org.creativelabs.ssa.visitor;

import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.*;
import japa.parser.ast.visitor.GenericVisitorAdapter;
import org.creativelabs.Constants;
import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.bool.FalseBooleanCondition;
import org.creativelabs.graph.condition.bool.TrueBooleanCondition;
import org.creativelabs.helper.GenericVisitorHelper;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.ssa.holder.MultiHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author azotcsit
 *         Date: 09.05.11
 *         Time: 15:10
 */
public class ExpressionStmtVisitor extends GenericVisitorAdapter<Expression, MultiHolder> {

    private boolean isNeededToIncreaseIndex;

    private InternalInstancesGraph graph;

    private String methodName;

    public ExpressionStmtVisitor(InternalInstancesGraph graph, String methodName) {
        this.graph = graph;
        this.methodName = methodName;
    }

    @Override
    public Expression visit(NameExpr n, MultiHolder arg) {
        String variableName = n.getName();

//        boolean isClassWithStaticMethod = Character.isUpperCase(variableName.charAt(0));
//        if (isClassWithStaticMethod) {
//            return n;
//        }

        Integer variableIndex = arg.read(variableName);
        if (variableIndex != null) {
            if (isNeededToIncreaseIndex) {
                variableIndex = arg.readFrom(variableName, false);
                variableIndex++;
                arg.write(variableName, variableIndex);
            }
            n.setName(variableName + Constants.SEPARATOR + variableIndex);
        } else {
            n.setName(variableName + " " + Constants.NOT_CONTAINS);
        }

        //TODO precoessing of static fields and methods

//        if (arg.containsFieldName(variableName)) {
//            return new FieldAccessExpr(new ThisExpr(), n.getName());
//        }

        return n;
    }

    @Override
    public Expression visit(BinaryExpr n, MultiHolder arg) {
        n.setRight(GenericVisitorHelper.visitExpression(n.getRight(), arg, this));
        n.setLeft(GenericVisitorHelper.visitExpression(n.getLeft(), arg, this));
        return n;
    }

    @Override
    public Expression visit(ArrayAccessExpr n, MultiHolder arg) {
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
    public Expression visit(ArrayCreationExpr n, MultiHolder arg) {
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
    public Expression visit(ArrayInitializerExpr expr, MultiHolder arg) {
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
    public Expression visit(ObjectCreationExpr n, MultiHolder arg) {
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
    public Expression visit(MethodCallExpr n, MultiHolder arg) {
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
        //TODO
        return n;
    }

    @Override
    public Expression visit(InstanceOfExpr n, MultiHolder arg) {
        if (n.getExpr() != null) {
            n.setExpr(GenericVisitorHelper.visitExpression(n.getExpr(), arg, this));
        }
        return n;
    }

    @Override
    public Expression visit(CastExpr n, MultiHolder arg) {
        if (n.getExpr() != null) {
            n.setExpr(GenericVisitorHelper.visitExpression(n.getExpr(), arg, this));
        }
        return n;
    }

    @Override
    public Expression visit(EnclosedExpr n, MultiHolder arg) {
        if (n.getInner() != null) {
            n.setInner(GenericVisitorHelper.visitExpression(n.getInner(), arg, this));
        }
        return n;
    }

    @Override
    public Expression visit(UnaryExpr n, MultiHolder arg) {
        if (n.getExpr() != null) {
            n.setExpr(GenericVisitorHelper.visitExpression(n.getExpr(), arg, this));
        }
        return n;
    }

    @Override
    public Expression visit(ThisExpr n, MultiHolder arg) {
        return n;
    }

    @Override
    public Expression visit(SuperExpr n, MultiHolder arg) {
        return n;
    }

    @Override
    public Expression visit(VariableDeclarationExpr n, MultiHolder arg) {
        List<VariableDeclarator> vars = n.getVars();
        for (VariableDeclarator variableDeclarator : vars) {
            String variableName = variableDeclarator.getId().getName();
            arg.write(variableName, 0);
            if (variableDeclarator.getInit() != null) {
                variableDeclarator.setInit(GenericVisitorHelper.visitExpression(variableDeclarator.getInit(), arg, this));
            }
            variableDeclarator.getId().setName(variableName + Constants.SEPARATOR + 0);

            String target = methodName + Constants.SEPARATOR + variableName;

            Condition[] conditions = null;
            if (variableDeclarator.getInit() != null) {
                Set<String> valuesNames = new TreeSet<String>();
                conditions = GenericVisitorHelper.visitExpression(
                        variableDeclarator.getInit(),
                        valuesNames,
                        new ConditionFinder(graph, methodName, arg.getModifiersHolder(), arg.getPhiNodesHolder()));
                for (String name : valuesNames) {
                    graph.addEdge(target + Constants.SEPARATOR + 0, name);

                }
            } else {
                conditions = new Condition[]{new TrueBooleanCondition(), new FalseBooleanCondition()};
            }

            if (conditions == null) {
                throw new IllegalStateException(variableDeclarator.getInit().getClass() + " is not supported by ConditionFinder.");
            }

            graph.addVertexConditions(target + Constants.SEPARATOR + 0,
                    conditions[0].and(arg.getBasicBlockCondition()),
                    conditions[1].and(arg.getBasicBlockCondition()));
        }

        n.setVars(vars);
        return n;
    }

    @Override
    public Expression visit(AssignExpr n, MultiHolder arg) {
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
            expressions.add(new NameExpr(arrayName + Constants.SEPARATOR + arg.read(arrayName)));
            expressions.add(new NameExpr(index));
            expressions.add(n.getValue());

            n.setValue(new MethodCallExpr(null, "Update", expressions));

            arg.increaseIndex(arrayName);
            n.setTarget(new NameExpr(arrayName + Constants.SEPARATOR + arg.read(arrayName)));

            String variableName = expressions.get(0).toString();
            String target = methodName + Constants.SEPARATOR + expressions.get(2).toString();

            Set<String> valuesNames = new TreeSet<String>();
            Condition[] conditions = GenericVisitorHelper.visitExpression(
                    n.getValue(),
                    valuesNames,
                    new ConditionFinder(graph, methodName, arg.getModifiersHolder(), arg.getPhiNodesHolder()));
            for (String name : valuesNames) {
                graph.addEdge(target + Constants.SEPARATOR + arg.read(variableName), name);

            }

            graph.addVertexConditions(target + Constants.SEPARATOR + arg.read(variableName),
                    conditions[0].and(arg.getBasicBlockCondition()),
                    conditions[1].and(arg.getBasicBlockCondition()));

        } else if (n.getTarget() instanceof NameExpr || n.getTarget() instanceof FieldAccessExpr) {
            n.setValue(GenericVisitorHelper.<Expression, MultiHolder>visitExpression(n.getValue(), arg, this));
            String variableName = n.getTarget().toString();

            isNeededToIncreaseIndex = true;
            n.setTarget(GenericVisitorHelper.<Expression, MultiHolder>visitExpression(n.getTarget(), arg, this));
            isNeededToIncreaseIndex = false;

            String target = methodName + Constants.SEPARATOR + variableName;

            Set<String> valuesNames = new TreeSet<String>();
            Condition[] conditions = GenericVisitorHelper.visitExpression(
                    n.getValue(),
                    valuesNames,
                    new ConditionFinder(graph, methodName, arg.getModifiersHolder(), arg.getPhiNodesHolder()));
            for (String name : valuesNames) {
                graph.addEdge(target + Constants.SEPARATOR + arg.read(variableName), name);

            }

            graph.addVertexConditions(target + Constants.SEPARATOR + arg.read(variableName),
                    conditions[0].and(arg.getBasicBlockCondition()),
                    conditions[1].and(arg.getBasicBlockCondition()));


        } else {
            throw new IllegalStateException("Class " + n.getTarget().getClass() + " is not supported " +
                    "by ExpressionStmtVisitor as assign target expression.");
        }

        return n;
    }


    @Override
    public Expression visit(FieldAccessExpr n, MultiHolder arg) {

        String name = n.getField();
        if (arg.read(name) != null) {
            if (isNeededToIncreaseIndex) {
                arg.increaseIndex(name);
                n.setField(name + Constants.SEPARATOR + arg.read(name));
            } else {
                n.setField(name + Constants.SEPARATOR + arg.read(name));
            }
        }

        if (Character.isUpperCase(n.getScope().toString().charAt(0))) {
            return n;
        }



        n.setScope(GenericVisitorHelper.visitExpression(n.getScope(), arg, this));
        return n;
    }

    @Override
    public Expression visit(ConditionalExpr n, MultiHolder arg) {
        n.setCondition(GenericVisitorHelper.visitExpression(n.getCondition(), arg, this));
        n.setThenExpr(GenericVisitorHelper.visitExpression(n.getThenExpr(), arg, this));
        n.setElseExpr(GenericVisitorHelper.visitExpression(n.getElseExpr(), arg, this));
        return n;
    }

    @Override
    public Expression visit(IntegerLiteralExpr n, MultiHolder arg) {
        return n;
    }

    @Override
    public Expression visit(IntegerLiteralMinValueExpr n, MultiHolder arg) {
        return n;
    }

    @Override
    public Expression visit(LongLiteralExpr n, MultiHolder arg) {
        return n;
    }

    @Override
    public Expression visit(LongLiteralMinValueExpr n, MultiHolder arg) {
        return n;
    }

    @Override
    public Expression visit(CharLiteralExpr n, MultiHolder arg) {
        return n;
    }

    @Override
    public Expression visit(BooleanLiteralExpr n, MultiHolder arg) {
        return n;
    }

    @Override
    public Expression visit(DoubleLiteralExpr n, MultiHolder arg) {
        return n;
    }

    @Override
    public Expression visit(StringLiteralExpr n, MultiHolder arg) {
        return n;
    }

    @Override
    public Expression visit(NullLiteralExpr n, MultiHolder arg) {
        return n;
    }
}
