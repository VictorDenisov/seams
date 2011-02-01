package org.creativelabs;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.visitor.GenericVisitorAdapter;

import java.util.*;

public class SsaFormBuilderVisitor extends GenericVisitorAdapter<StringBuilder, VariablesHolder> {

    private static final String UNSUPPORTED = "Unsupported expression: ";

    @Override
    public StringBuilder visit(VariableDeclarationExpr n, VariablesHolder arg) {
        String variableName = n.getVars().get(0).getId().getName();
        arg.write(variableName, 0);
        String rightPart = new SsaFinder(arg, false).determineSsa(n.getVars().get(0).getInit());
        return new StringBuilder(variableName + 0 + " <- " + rightPart + "\n");
    }

    @Override
    public StringBuilder visit(AssignExpr n, VariablesHolder arg) {
        String rightPart = new SsaFinder(arg, false).determineSsa(n.getValue());
        String leftPart = new SsaFinder(arg, true).determineSsa(n.getTarget());
        return new StringBuilder(leftPart + " <- " + rightPart + "\n");
    }

    @Override
    public StringBuilder visit(MethodCallExpr n, VariablesHolder arg) {
        return new StringBuilder(new SsaFinder(arg, false).determineSsa(n) + "\n");
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
        if (elseStmt != null) {
            elseBlock.append(getStmt(elseStmt, elseVariables));
        } else {
            elseVariables = arg.copy();
        }


        StringBuilder buffer = new StringBuilder();
        buffer.append("if " + condition + "\n");
        buffer.append("then do\n");
        buffer.append(thenBlock);
        buffer.append("end\n");
        buffer.append("else do\n");
        buffer.append(elseBlock);
        buffer.append("end\n");

        for (String name : elseVariables.getDifferenceInVariables(thenVariables, false)) {
            buffer.append(name + (Math.max(thenVariables.read(name), elseVariables.read(name)) + 1) + " <- " + elseVariables.getPhi(thenVariables, name) + "\n");
            elseVariables.write(name, Math.max(thenVariables.read(name), elseVariables.read(name)) + 1);
        }

        arg.mergeHolders(thenVariables, elseVariables);

        return buffer;
    }

    @Override
    public StringBuilder visit(ForStmt n, VariablesHolder arg) {

        VariablesHolder holder1 = arg.copy();

        List<StringBuilder> init = new ArrayList<StringBuilder>();
        for (Expression expression : n.getInit()) {
            init.add(getExpr(expression, holder1));
        }

        String cmp = new SsaFinder(holder1, false).determineSsa((BinaryExpr) n.getCompare());

        VariablesHolder holder2 = holder1.copy();

        Set<String> usingVariables = new HashSet<String>();
        new AssignVisitor().visit(n, usingVariables);

        for (String var : usingVariables) {
            holder2.increaseIndex(var);
        }

        StringBuilder body = getStmt(n.getBody(), holder2);

        List<StringBuilder> update = new ArrayList<StringBuilder>();
        for (Expression expression : n.getUpdate()) {
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

        for (String name : usingVariables) {
            if (holder2.containsKey(name, true) && arg.containsKey(name, true)) {
                buffer.append(name + (Math.max(holder2.read(name), arg.read(name)) + 1) + " <- " + holder2.getPhi(arg, name) + "\n");
                arg.write(name, (Math.max(holder2.read(name), arg.read(name)) + 1));
            }
        }

        arg.mergeHolders(holder1, holder2);

        return buffer;
    }

    @Override
    public StringBuilder visit(WhileStmt n, VariablesHolder arg) {

        String cmp = new SsaFinder(arg, false).determineSsa(n.getCondition());

        VariablesHolder holder = arg.copy();


        Set<String> usingVariables = new HashSet<String>();
        new AssignVisitor().visit(n, usingVariables);

        for (String var : usingVariables) {
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

        for (String name : usingVariables) {
            if (holder.containsKey(name, true) && arg.containsKey(name, true)) {
                buffer.append(name + (Math.max(holder.read(name), arg.read(name)) + 1) + " <- " + holder.getPhi(arg, name) + "\n");
                arg.write(name, (Math.max(holder.read(name), arg.read(name)) + 1));
            }
        }

        arg.mergeHolders(holder);

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
        if (n != null && n.getStmts() != null) {
            for (Statement statement : n.getStmts()) {
                builder.append(getStmt(statement, arg));
            }
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
        VariablesHolder holder = new VariablesHolder(map);

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

    private StringBuilder getExpr(Expression expression, VariablesHolder arg) {
        if (expression instanceof AssignExpr) {
            return visit((AssignExpr) expression, arg);
        } else if (expression instanceof VariableDeclarationExpr) {
            return visit((VariableDeclarationExpr) expression, arg);
        } else if (expression instanceof MethodCallExpr) {
            return visit((MethodCallExpr) expression, arg);
        }
        return new StringBuilder(UNSUPPORTED + expression.toString() + "\n");
    }

}
