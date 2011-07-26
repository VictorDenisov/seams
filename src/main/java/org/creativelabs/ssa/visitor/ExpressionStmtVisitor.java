package org.creativelabs.ssa.visitor;

import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.*;
import japa.parser.ast.visitor.GenericVisitorAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.creativelabs.ClassProcessor;
import org.creativelabs.Constants;
import org.creativelabs.helper.GenericVisitorHelper;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.ssa.holder.MethodArgsHolder;
import org.creativelabs.ssa.holder.MultiHolder;
import org.creativelabs.ssa.holder.variable.StringVariable;
import org.creativelabs.ssa.holder.variable.Variable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author azotcsit
 *         Date: 09.05.11
 *         Time: 15:10
 */
public class ExpressionStmtVisitor extends GenericVisitorAdapter<Expression, MultiHolder> {

    Log log = LogFactory.getLog(ExpressionStmtVisitor.class);

    private boolean isNeededToIncreaseIndex;

    private InternalInstancesGraph graph;

    private String methodName;

    private String className;

    public ExpressionStmtVisitor(InternalInstancesGraph graph, String methodName, String className) {
        this.graph = graph;
        this.methodName = methodName;
        this.className = className;
    }

    private boolean isCamelStyleClassName(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < s.length(); i++) {
            if (Character.isLowerCase(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean isCamelStyleStaticVariable(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isLowerCase(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Expression visit(NameExpr n, MultiHolder arg) {
        String variableName = n.getName();
        Variable variable;
        if (arg.getMethodArgsHolder().containsArgName(variableName)) {
            variable = new StringVariable(n.getName(), Constants.ARG_SCOPE);
        } else {
            if (arg.getFieldsHolder().containsCreated(variableName)) {
                variable = new StringVariable(n.getName(), Constants.EMPTY_SCOPE);
            } else {
                variable = new StringVariable(n.getName(), Constants.THIS_SCOPE);
            }
        }

        if (isCamelStyleClassName(variableName)) {
            return n;
        }

        //TODO to implement more smart processing of static variables

        if (isCamelStyleStaticVariable(variableName)) {
            n.setName(variableName + Constants.SEPARATOR + 0);
            return n;
        }

        Integer variableIndex = arg.read(variable);
        if (variableIndex != null) {
            if (isNeededToIncreaseIndex) {
                variableIndex = arg.readFrom(variable, false);
                variableIndex++;
                arg.write(variable, variableIndex);
            }
            n.setName(variableName + Constants.SEPARATOR + variableIndex);
        } else {

            log.info(ClassProcessor.debugInfo + "variable [name=" + variableName
                    + "] is not contain in variables list.");
//            n.setName(variable.getString() + Constants.SEPARATOR + "0");
        }

        //TODO precoessing of static fields and methods

        if (arg.containsFieldName(variableName) && !arg.containsArgName(variableName)) {
            arg.write(variable, 0);
            return new FieldAccessExpr(new ThisExpr(), n.getName());
        }

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
            arg.getFieldsHolder().addCreated(variableName);
            Variable variable = new StringVariable(variableName, Constants.EMPTY_SCOPE);
            if (arg.read(variable) == null) {
                arg.write(variable, 0);
            } else {
//                arg.increaseIndex(variable);
            }
            if (variableDeclarator.getInit() != null) {
                variableDeclarator.setInit(GenericVisitorHelper.visitExpression(variableDeclarator.getInit(), arg, this));
            }
//            variableDeclarator.getId().setName(variableName + Constants.SEPARATOR + arg.read(variable));
            variableDeclarator.getId().setName(variableName + Constants.SEPARATOR + 0);

            String target = methodName + Constants.SEPARATOR + variableName;

            //TODO
//            Condition[] conditions;
//            if (variableDeclarator.getInit() != null) {
//                Set<String> valuesNames = new TreeSet<String>();
//                conditions = GenericVisitorHelper.visitExpression(
//                        variableDeclarator.getInit(),
//                        valuesNames,
//                        new ConditionFinder(graph, methodName, className, arg.getModifiersHolder(), arg.getVariablesHolder(), arg.getMethodArgsHolder(), arg.getPhiNodesHolder()));
//                for (String name : valuesNames) {
//                    graph.addEdge(target + Constants.SEPARATOR + 0, methodName + Constants.SEPARATOR + name);
//
//                }
//            } else {
//                conditions = new Condition[]{new TrueBooleanCondition(), new FalseBooleanCondition()};
//            }
//
//            if (conditions == null) {
//                log.error(variableDeclarator.getInit().getClass() + " is not supported by ConditionFinder.");
//                conditions = new Condition[]{new TrueBooleanCondition(), new FalseBooleanCondition()};
//            }
//
//            graph.addVertexConditions(target + Constants.SEPARATOR + 0,
//                    conditions[0].and(arg.getBasicBlockCondition()),
//                    conditions[1].and(arg.getBasicBlockCondition()));
        }

        n.setVars(vars);
        return n;
    }

    @Override
    public Expression visit(AssignExpr n, MultiHolder arg) {
        if (n.getTarget() instanceof ArrayAccessExpr) {
            String arrayName = null;
            Variable variable;
            if (((ArrayAccessExpr) n.getTarget()).getName() instanceof FieldAccessExpr) {
                arrayName = ((FieldAccessExpr) ((ArrayAccessExpr) n.getTarget()).getName()).getField();
                String scope = ((FieldAccessExpr) ((ArrayAccessExpr) n.getTarget()).getName()).getScope().toString();
                variable = new StringVariable(arrayName, scope);
            } else {
                arrayName = ((NameExpr) ((ArrayAccessExpr) n.getTarget()).getName()).getName();
                if (arg.getMethodArgsHolder().containsArgName(arrayName)) {
                    variable = new StringVariable(arrayName, Constants.ARG_SCOPE);
                } else {
                    if (arg.getFieldsHolder().containsCreated(arrayName)) {
                        variable = new StringVariable(arrayName, Constants.EMPTY_SCOPE);
                    } else {
                        variable = new StringVariable(arrayName, Constants.THIS_SCOPE);
                    }
                }
            }

            ((ArrayAccessExpr) n.getTarget()).setIndex(GenericVisitorHelper.visitExpression(
                    ((ArrayAccessExpr) n.getTarget()).getIndex(), arg, this));
            n.setValue(GenericVisitorHelper.visitExpression(n.getValue(), arg, this));

            String index = ((ArrayAccessExpr) n.getTarget()).getIndex().toString();

            List<Expression> expressions = new ArrayList<Expression>();
            expressions.add(new NameExpr(arrayName + Constants.SEPARATOR + arg.read(variable)));
            expressions.add(new NameExpr(index));
            expressions.add(n.getValue());

            n.setValue(new MethodCallExpr(null, "#Update", expressions));

            arg.increaseIndex(variable);
            n.setTarget(new NameExpr(arrayName + Constants.SEPARATOR + arg.read(variable)));

            String variableName = expressions.get(0).toString();
            String target = methodName + Constants.SEPARATOR + expressions.get(2).toString();

            //TODO
//            Set<String> valuesNames = new TreeSet<String>();
//            Condition[] conditions = GenericVisitorHelper.visitExpression(
//                    n.getValue(),
//                    valuesNames,
//                    new ConditionFinder(graph, methodName, className, arg.getModifiersHolder(), arg.getVariablesHolder(), arg.getMethodArgsHolder(), arg.getPhiNodesHolder()));
//            for (String name : valuesNames) {
//                graph.addEdge(target + Constants.SEPARATOR + arg.read(variable), methodName + Constants.SEPARATOR + name);
//
//            }
//
//            graph.addVertexConditions(target + Constants.SEPARATOR + arg.read(variable),
//                    conditions[0].and(arg.getBasicBlockCondition()),
//                    conditions[1].and(arg.getBasicBlockCondition()));

        } else if (n.getTarget() instanceof NameExpr || n.getTarget() instanceof FieldAccessExpr) {
            n.setValue(GenericVisitorHelper.<Expression, MultiHolder>visitExpression(n.getValue(), arg, this));
            String variableName = n.getTarget().toString();
            Variable variable;
            if (arg.getMethodArgsHolder().containsArgName(variableName)) {
                variable = new StringVariable(variableName, Constants.ARG_SCOPE);
            } else {
                if (arg.getFieldsHolder().containsCreated(variableName)) {
                    variable = new StringVariable(variableName, Constants.EMPTY_SCOPE);
                } else {
                    variable = new StringVariable(variableName, Constants.THIS_SCOPE);
                }
            }

            isNeededToIncreaseIndex = true;
            n.setTarget(GenericVisitorHelper.<Expression, MultiHolder>visitExpression(n.getTarget(), arg, this));
            isNeededToIncreaseIndex = false;

            String target = methodName + Constants.SEPARATOR + variableName;

            //TODO
//            Set<String> valuesNames = new TreeSet<String>();
//            Condition[] conditions = GenericVisitorHelper.visitExpression(
//                    n.getValue(),
//                    valuesNames,
//                    new ConditionFinder(graph, methodName, className, arg.getModifiersHolder(), arg.getVariablesHolder(), arg.getMethodArgsHolder(), arg.getPhiNodesHolder()));
//            for (String name : valuesNames) {
//                graph.addEdge(target + Constants.SEPARATOR + arg.read(variable), methodName + Constants.SEPARATOR + name);
//
//            }
//
//            graph.addVertexConditions(target + Constants.SEPARATOR + arg.read(variable),
//                    conditions[0].and(arg.getBasicBlockCondition()),
//                    conditions[1].and(arg.getBasicBlockCondition()));
//

        } else {
            throw new IllegalStateException("Class " + n.getTarget().getClass() + " is not supported " +
                    "by ExpressionStmtVisitor as assign target expression.");
        }

        return n;
    }


    @Override
    public Expression visit(FieldAccessExpr n, MultiHolder arg) {

        //TODO to implement more smart processing of fields

        String name = n.getField();
        String scope = n.getScope().toString();

        Variable variable = createVariable(name, scope, arg.getMethodArgsHolder());

        if (arg.read(variable) != null) {
            if (isNeededToIncreaseIndex) {
                arg.increaseIndex(variable);
                n.setField(name + Constants.SEPARATOR + arg.read(variable));
            } else {
                n.setField(name + Constants.SEPARATOR + arg.read(variable));
            }
        } else {
            n.setField(name + Constants.SEPARATOR + 0);
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

    private Variable createVariable(String name, String scope, MethodArgsHolder methodArgsHolder) {
        if (scope == null || scope.isEmpty()) {
            if (methodArgsHolder.containsArgName(name)) {
                return new StringVariable(name, Constants.ARG_SCOPE);
            } else {
                return new StringVariable(name, Constants.THIS_SCOPE);
            }
        } else {
            return new StringVariable(name, scope);
        }
    }
}
