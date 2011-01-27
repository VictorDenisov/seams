package org.creativelabs;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.*;
import japa.parser.ast.visitor.GenericVisitorAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SsaFormBuilderVisitor extends GenericVisitorAdapter<StringBuilder, VariablesHolder> {

    private static final String UNSUPPORTED = "Unsupported expression: ";

    @Override
    public StringBuilder visit(VariableDeclarationExpr n, VariablesHolder arg) {
        String variableName = n.getVars().get(0).getId().getName();
        arg.write(variableName, 0);
        return new StringBuilder(variableName + 0 + " <- " + n.getVars().get(0).getInit().toString() + "\n");
    }

    @Override
    public StringBuilder visit(AssignExpr n, VariablesHolder arg) {
        String variableName = n.getTarget().toString();
        if (arg.containsKey(variableName, false)) {
            String rightPart = new SsaFinder(arg, false).determineSsa(n.getValue());
            String leftPart = new SsaFinder(arg, true).determineSsa(n.getTarget());
            return new StringBuilder(leftPart + " <- " + rightPart + "\n");
        }
        return new StringBuilder(UNSUPPORTED + " <not contain in key set> " + n.toString() + "\n");
    }

    @Override
    public StringBuilder visit(IfStmt n, VariablesHolder arg) {
        String condition = new SsaFinder(arg, false).determineSsa(n.getCondition());

        VariablesHolder thenVariables = arg.copy();

        StringBuilder thenBlock = new StringBuilder();
        Statement thenStmt = n.getThenStmt();
        thenBlock.append(getStmt(thenStmt, thenVariables));

        VariablesHolder elseVariables = thenVariables.copy();
        elseVariables.setReadVariables(arg.getReadVariables());

        StringBuilder elseBlock = new StringBuilder();
        Statement elseStmt = n.getElseStmt();
        elseBlock.append(getStmt(elseStmt, elseVariables));



        StringBuilder buffer = new StringBuilder();
        buffer.append("if " + condition + "\n");
        buffer.append("then do\n");
        buffer.append(thenBlock);
        buffer.append("end\n");
        buffer.append("else do\n");
        buffer.append(elseBlock);
        buffer.append("end\n");

        for (String name : elseVariables.getDifferenceInVariables(thenVariables, false)) {
            buffer.append(elseVariables.getPhi(thenVariables, name) + "\n");
        }

        return buffer;
    }

    @Override
    public StringBuilder visit(ForStmt n, VariablesHolder arg) {

        VariablesHolder holder1 = arg.copy();

        List<StringBuilder> init = new ArrayList<StringBuilder>();
        for (Expression expression : n.getInit()){
            init.add(getExpr(expression, holder1));
        }

        String cmp = new SsaFinder(holder1, false).determineSsa((BinaryExpr) n.getCompare());

        VariablesHolder holder2 = holder1.copy();

        List<String> usingVariables = new ArrayList<String>();
        new AssignVisitor().visit(n, usingVariables);

        for (String var : usingVariables){
            holder2.increaseIndex(var);
        }

        StringBuilder body = getStmt(n.getBody(), holder2);

        List<StringBuilder> update = new ArrayList<StringBuilder>();
        for (Expression expression : n.getUpdate()){
            update.add(getExpr(expression, holder2));
        }



        StringBuilder buffer = new StringBuilder();

        for (StringBuilder builder : init) {
            buffer.append(builder);
        }
        buffer.append("repeat\n");
        buffer.append("begin\n");

        for (String name : holder2.getDifferenceInVariables(holder1, false)) {
            buffer.append(name + (holder1.read(name) + 1) + " <- " + holder2.getPhi(holder1, name) + "\n");
        }

        buffer.append(body);

        for (StringBuilder builder : update) {
            buffer.append(builder);
        }


        buffer.append("end\n");
        buffer.append("until(" + cmp + ")\n");

        return buffer;
    }

    @Override
    public StringBuilder visit(WhileStmt n, VariablesHolder arg) {

        String cmp = new SsaFinder(arg, false).determineSsa((BinaryExpr) n.getCondition());

        VariablesHolder holder = arg.copy();


        List<String> usingVariables = new ArrayList<String>();
        new AssignVisitor().visit(n, usingVariables);

        for (String var : usingVariables){
            holder.increaseIndex(var);
        }

        StringBuilder body = getStmt(n.getBody(), holder);

        StringBuilder buffer = new StringBuilder();

        buffer.append("repeat\n");
        buffer.append("begin\n");

        for (String name : holder.getDifferenceInVariables(arg, false)) {
            buffer.append(name + (arg.read(name) + 1) + " <- " + holder.getPhi(arg, name) + "\n");
        }


        buffer.append(body);

        buffer.append("end\n");
        buffer.append("until(" + cmp + ")\n");

        return buffer;
    }

    @Override
    public StringBuilder visit(ExpressionStmt n, VariablesHolder arg) {
        Expression expression = n.getExpression();
        return getExpr(expression, arg);
    }


    @Override
    public StringBuilder visit(BlockStmt n, VariablesHolder arg) {
        StringBuilder builder = new StringBuilder();
        for (Statement statement : n.getStmts()) {
            builder.append(getStmt(statement, arg));
        }
        return builder;
    }

    @Override
    public StringBuilder visit(MethodDeclaration n, VariablesHolder arg) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        if (n.getParameters() != null) {
            for (Parameter parameter : n.getParameters()) {
                map.put(parameter.getId().getName(), 0);
            }
        }
        VariablesHolder holder = new VariablesHolder(map, VariablesHolder.Mode.READ_R_VARS_WRITE_WR_VARS);
        holder.copyWriteToReadVariables();

        return visit(n.getBody(), holder);
    }


    private StringBuilder getStmt(Statement statement, VariablesHolder arg) {
        if (statement instanceof BlockStmt) {
            return visit((BlockStmt) statement, arg);
        } else if (statement instanceof ExpressionStmt) {
            return visit((ExpressionStmt) statement, arg);
        } else if (statement instanceof IfStmt) {
            return visit((IfStmt) statement, arg);
        } else if (statement instanceof ForStmt) {
            return visit((ForStmt) statement, arg);
        } else if (statement instanceof WhileStmt) {
            return visit((WhileStmt) statement, arg);
        }
        return new StringBuilder(UNSUPPORTED + statement.toString() + "\n");
    }

    private StringBuilder getExpr(Expression expression, VariablesHolder arg){
        if (expression instanceof AssignExpr) {
            return visit((AssignExpr) expression, arg);
        } else if (expression instanceof VariableDeclarationExpr) {
            return visit((VariableDeclarationExpr) expression, arg);
        }
        return new StringBuilder(UNSUPPORTED + expression.toString() + "\n");
    }

}
