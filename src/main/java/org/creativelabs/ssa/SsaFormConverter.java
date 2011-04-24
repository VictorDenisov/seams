package org.creativelabs.ssa;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.iig.SimpleInternalInstancesGraph;

import java.util.*;

/**
 * Uses for modification ast expressions and statements.
 */
public class SsaFormConverter extends VoidVisitorAdapter<VariablesHolder> {

    public static final String SEPARATOR = "#";

    private InternalInstancesGraph graph = new SimpleInternalInstancesGraph();

    private MethodDeclaration methodDeclaration;

    public SsaFormConverter() {
    }

    public SsaFormConverter(InternalInstancesGraph graph) {
        this.graph = graph;
    }

    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }

    public InternalInstancesGraph getGraph() {
        return graph;
    }

    public void addToGraph(String from, String to) {
        graph.add(from, to);
    }

    @Override
    public void visit(VariableDeclarationExpr n, VariablesHolder arg) {
        List<VariableDeclarator> vars = n.getVars();
        for (VariableDeclarator variableDeclarator : vars) {
            String variableName = variableDeclarator.getId().getName();
            arg.write(variableName, 0);
            variableDeclarator.setInit(new SsaFinder(arg, false).determineSsa(variableDeclarator.getInit()));
            variableDeclarator.getId().setName(variableName + SEPARATOR + 0);
            graph.add(methodDeclaration.getName() + SEPARATOR + variableName + SEPARATOR + 0,
                    methodDeclaration.getName() + SEPARATOR + variableDeclarator.getInit().toString());
        }


        n.setVars(vars);
    }

    @Override
    public void visit(AssignExpr n, VariablesHolder arg) {
        if (n.getTarget() instanceof ArrayAccessExpr) {
            String arrayName = ((NameExpr) ((ArrayAccessExpr) n.getTarget()).getName()).getName();

            new SsaFinder(arg, false).determineSsa(((ArrayAccessExpr) n.getTarget()).getIndex());

            String index = ((ArrayAccessExpr) n.getTarget()).getIndex().toString();

            List<Expression> expressions = new ArrayList<Expression>();
            expressions.add(new NameExpr(arrayName + SEPARATOR + arg.read(arrayName)));
            expressions.add(new NameExpr(index));
            expressions.add(n.getValue());

            n.setValue(new MethodCallExpr(null, "Update", expressions));

            arg.increaseIndex(arrayName);
            n.setTarget(new NameExpr(arrayName + SEPARATOR + arg.read(arrayName)));
            graph.add(methodDeclaration.getName() + SEPARATOR + arrayName + SEPARATOR + arg.read(arrayName),
                    methodDeclaration.getName() + SEPARATOR + expressions.get(0).toString());
        } else {
            n.setValue(new SsaFinder(arg, false).determineSsa(n.getValue()));
            n.setTarget(new SsaFinder(arg, true).determineSsa(n.getTarget()));
            graph.add(methodDeclaration.getName() + SEPARATOR + n.getTarget().toString(),
                    methodDeclaration.getName() + SEPARATOR + n.getTarget().toString());
        }
    }

    @Override
    public void visit(MethodCallExpr n, VariablesHolder arg) {
        new SsaFinder(arg, false).determineSsa(n);
    }

    @Override
    public void visit(CastExpr n, VariablesHolder arg) {
        new SsaFinder(arg, false).determineSsa(n.getExpr());
    }

    @Override
    public void visit(IfStmt n, VariablesHolder arg) {
        new SsaFinder(arg, false).determineSsa(n.getCondition());

        VariablesHolder thenVariables = arg.copy();

        Set<String> assigningVariables = new HashSet<String>();
        new AssignVisitor().visit(n, assigningVariables);

        Statement thenStmt = n.getThenStmt();
        getStmt(thenStmt, thenVariables);

        VariablesHolder elseVariables = thenVariables.copy();
        elseVariables.setReadVariables(arg.getReadVariables());

        Statement elseStmt = n.getElseStmt();
        if (elseStmt != null) {
            getStmt(elseStmt, elseVariables);
        } else {
            elseVariables = arg.copy();
        }

        List<String> vars = new ArrayList<String>(assigningVariables);
        Collections.sort(vars);

        for (String name : vars) {
            Integer newIndex = Math.max(thenVariables.read(name) == null ? -1 : thenVariables.read(name), elseVariables.read(name) == null ? -1 : elseVariables.read(name)) + 1;
            addPhi(n, new PhiNode(name, newIndex, PhiNode.Mode.AFTER, elseVariables.getPhiIndexes(thenVariables, name)));
            elseVariables.write(name, newIndex);
        }

        arg.mergeHolders(thenVariables, elseVariables);
    }

//    @Override
//    public void visit(ForStmt n, VariablesHolder arg) {
////         n = new CopyingUtils<ForStmt>().copy(n);
//        //with init variables
//        VariablesHolder holder1 = arg.copy();
//
//        for (Expression expression : n.getInit()) {
//            getExpr(expression, holder1);
//        }
//
//        VariablesHolder holder2 = holder1.copy();
//
//        Set<String> usingVariables = new HashSet<String>();
//        new AssignVisitor().visit(n, usingVariables);
//
//        for (String var : usingVariables) {
//            holder2.increaseIndex(var);
//        }
//
//
//        for (Expression expression : n.getUpdate()) {
//            getExpr(expression, holder2);
//        }
//
//        //TODO almost always returns null
//        Statement body = n.getBody();
//                //= new CopyingUtils<Statement>().copy(n.getBody());
//
//        VariablesHolder holder3 = holder2.copy();
//
//        getStmt(body, holder3);
//
//        for (String name : holder3.getDifferenceInVariables(holder1, false)) {
////            preparePhi(name + SEPARATOR + (holder1.read(name) + 1), false, holder3.getPhi(holder1, name));
//        }
//
//        getStmt(n.getBody(), holder2);
//
//        new SsaFinder(holder1, false).determineSsa((BinaryExpr) n.getCompare());
//        List<Expression> phisInCondition = new ArrayList<Expression>();
////        String[] s = holder1.getPhi(holder2, "i");
////        phisInCondition.add(new NameExpr(s[0]));
////        phisInCondition.add(new NameExpr(s[1]));
//        ((BinaryExpr) n.getCompare()).setLeft(
//                new MethodCallExpr(
//                        null,
//                        "phi",
//                        phisInCondition));
//
//
//        for (String name : usingVariables) {
//            if (holder2.containsKey(name, true) && arg.containsKey(name, true)) {
////                preparePhi(name + SEPARATOR + (Math.max(holder2.read(name), arg.read(name)) + 1), holder2.getPhi(arg, name));
//                arg.write(name, (Math.max(holder2.read(name), arg.read(name)) + 1));
//            }
//        }
//
//        arg.mergeHolders(holder1, holder2);
////        return n;
//    }

    @Override
    public void visit(WhileStmt n, VariablesHolder arg) {

        VariablesHolder holder = arg.copy();

        Set<String> assigningVariables = new HashSet<String>();
        new AssignVisitor().visit(n, assigningVariables);

        Set<String> conditionsVars = new HashSet<String>();
        new NameVisitor().visit((BinaryExpr) n.getCondition(), conditionsVars);

        //phi nodes with only one variable's index
        Set<PhiNode> beforeWhilePhiNodes = new HashSet<PhiNode>();

        for (String var : assigningVariables) {
            holder.increaseIndex(var);
            if (assigningVariables.contains(var)) {
                beforeWhilePhiNodes.add(new PhiNode(var, arg.read(var) + 1, PhiNode.Mode.BEFORE, arg.read(var)));
                arg.increaseIndex(var);
            }
        }

        new SsaFinder(arg, false).determineSsa(n.getCondition());

        for (String var : assigningVariables) {
            if (assigningVariables.contains(var)) {
                holder.increaseIndex(var);
            }
        }

        //phi nodes with only one variable's index
        Set<PhiNode> inWhilePhiNodes = new HashSet<PhiNode>();

        for (String name : holder.getDifferenceInVariables(arg, false)) {
            inWhilePhiNodes.add(new PhiNode(name, holder.read(name), PhiNode.Mode.BEFORE, arg.read(name)));
        }

        addAllPhi(((BlockStmt) n.getBody()).getStmts().get(0), inWhilePhiNodes);
        addAllPhi(n, beforeWhilePhiNodes);

        getStmt(n.getBody(), holder);

        for (PhiNode node : beforeWhilePhiNodes) {
            node.addIndex(holder.read(node.getName()));
        }

        for (PhiNode node : inWhilePhiNodes) {
            node.addIndex(holder.read(node.getName()));
            node.addIndexToExprStmt(holder.read(node.getName()));
        }

        for (String name : assigningVariables) {
            if (holder.containsKey(name, true) && arg.containsKey(name, true)) {
                addPhi(n, new PhiNode(name, holder.read(name) + 1, PhiNode.Mode.AFTER, holder.getPhiIndexes(arg, name)));
                arg.write(name, (Math.max(holder.read(name), arg.read(name)) + 1));
            }
        }

        arg.mergeHolders(holder);
    }

    @Override
    public void visit(ExpressionStmt n, VariablesHolder arg) {
        getExpr(n.getExpression(), arg);
    }


    @Override
    public void visit(BlockStmt n, VariablesHolder arg) {
        if (n != null && n.getStmts() != null) {
            List<Statement> statements = new ArrayList<Statement>();
            for (Statement statement : n.getStmts()) {

                //changing statement
                getStmt(statement, arg);

                //inserting phi functions if needed
                for (PhiNode phiNode : statement.getPhiNodes()) {
                    if (PhiNode.Mode.BEFORE.equals(phiNode.getMode())) {
                        statements.add(phiNode.convertToExprStmt());
                    }
                }

                //inserting statement
                statements.add(statement);

                //inserting phi functions if needed
                for (PhiNode phiNode : statement.getPhiNodes()) {
                    if (PhiNode.Mode.AFTER.equals(phiNode.getMode())) {
                        statements.add(phiNode.convertToExprStmt());
                    }
                }

                statement.removePhiNodes();
            }
            n.setStmts(statements);
        }
    }

    @Override
    public void visit(MethodDeclaration n, VariablesHolder arg) {
        //TODO copy of n
//        n = new CopyingUtils<MethodDeclaration>().copy(n);
        methodDeclaration = n;
        Map<String, Integer> map = new HashMap<String, Integer>();
        if (n.getParameters() != null) {
            for (Parameter parameter : n.getParameters()) {
                graph.add(n.getName() + SsaFormConverter.SEPARATOR + parameter.getId().getName(),
                        parameter.getId().getName() + SsaFormConverter.SEPARATOR + 0);
                map.put(parameter.getId().getName(), 0);
                parameter.getId().setName(parameter.getId().getName() + SEPARATOR + 0);
            }
        }
        VariablesHolder holder = new VariablesHolder(map);
        if (arg != null) {
            holder.mergeHolders(arg);
        }
        visit(n.getBody(), holder);

        methodDeclaration = n;
    }

    @Override
    public void visit(ReturnStmt n, VariablesHolder arg) {
        new SsaFinder(arg, false).determineSsa(n.getExpr());
    }

    @Override
    public void visit(TryStmt n, VariablesHolder arg) {
        getStmt(n.getTryBlock(), arg);

        for (CatchClause catchClause : n.getCatchs()) {
            VariablesHolder holder = arg.copy();
            holder.write(catchClause.getExcept().getId().getName(), 0);
            new SsaFinder(holder, false).determineSsa(catchClause.getExcept().getId());
            visit(catchClause.getCatchBlock(), holder);
        }

        if (n.getFinallyBlock() != null) {
            visit(n.getFinallyBlock(), arg);
        }
    }

    @Override
    public void visit(ForeachStmt n, VariablesHolder arg) {

        Set<String> assigningVariables = new HashSet<String>();
        new AssignVisitor().visit(n, assigningVariables);

        Set<String> conditionsVars = new HashSet<String>();
        new NameVisitor().visit((VariableDeclarationExpr) n.getVariable(), conditionsVars);

        VariablesHolder holder = arg.copy();

        new SsaFinder(holder, false).determineSsa(n.getIterable());
        visit(n.getVariable(), holder);

        n.getVariable().getVars().get(0).setInit(new MethodCallExpr(n.getIterable(), "#next"));


        for (String var : assigningVariables) {
            if (!conditionsVars.contains(var)) {
                holder.increaseIndex(var);
            }
        }

        //phi nodes with only one variable's index
        Set<PhiNode> inWhilePhiNodes = new HashSet<PhiNode>();

        for (String name : assigningVariables) {
            if (!conditionsVars.contains(name)) {
                inWhilePhiNodes.add(new PhiNode(name, holder.read(name), PhiNode.Mode.BEFORE, arg.read(name)));
            }
        }

        if (((BlockStmt) n.getBody()).getStmts() != null) {
            addAllPhi(((BlockStmt) n.getBody()).getStmts().get(0), inWhilePhiNodes);
            getStmt(n.getBody(), holder);
        }

        for (PhiNode node : inWhilePhiNodes) {
            if (holder.read(node.getName()) != null) {
                node.addIndex(holder.read(node.getName()));
                node.addIndexToExprStmt(holder.read(node.getName()));
            }
        }

        for (String name : assigningVariables) {
            if (holder.containsKey(name, true) && arg.containsKey(name, true)) {
                addPhi(n, new PhiNode(name, holder.read(name) + 1, PhiNode.Mode.AFTER, holder.getPhiIndexes(arg, name)));
                arg.write(name, (Math.max(holder.read(name), arg.read(name)) + 1));
            }
        }
    }

    /**
     * Modifies the statement.
     *
     * @param statement of type Statement
     * @param arg       of type VariablesHolder
     */
    private void getStmt(Statement statement, VariablesHolder arg) {
        if (statement instanceof BlockStmt) {
            visit((BlockStmt) statement, arg);
        } else if (statement instanceof ExpressionStmt) {
            visit((ExpressionStmt) statement, arg);
        } else if (statement instanceof IfStmt) {
            visit((IfStmt) statement, arg);
        } else if (statement instanceof ForStmt) {
            visit((ForStmt) statement, arg);
        } else if (statement instanceof WhileStmt) {
            visit((WhileStmt) statement, arg);
        } else if (statement instanceof ReturnStmt) {
            visit((ReturnStmt) statement, arg);
        } else if (statement instanceof TryStmt) {
            visit((TryStmt) statement, arg);
        } else if (statement instanceof ForeachStmt) {
            visit((ForeachStmt) statement, arg);
        }
        //TODO remove null return value
    }

    /**
     * Modifies the expression.
     *
     * @param expression of type Expression
     * @param arg        of type VariablesHolder
     */
    private void getExpr(Expression expression, VariablesHolder arg) {
        if (expression instanceof AssignExpr) {
            visit((AssignExpr) expression, arg);
        } else if (expression instanceof VariableDeclarationExpr) {
            visit((VariableDeclarationExpr) expression, arg);
        } else if (expression instanceof MethodCallExpr) {
            visit((MethodCallExpr) expression, arg);
        } else if (expression instanceof CastExpr) {
            visit((CastExpr) expression, arg);
        }
    }

    /**
     * Returns list of phi nodes for statement.
     *
     * @param stmt of type Statement
     * @return Set<PhiNode>
     */
    private Set<PhiNode> getPhiFunctions(Statement stmt) {
        return stmt.getPhiNodes();
    }

    /**
     * Adds phi node to statement.
     *
     * @param stmt    of type Statement
     * @param phiNode of type PhiNode
     */
    private void addPhi(Statement stmt, PhiNode phiNode) {
        stmt.addPhi(phiNode);
    }

    /**
     * Adds phi nodes to statement.
     *
     * @param stmt     of type Statement
     * @param phiNodes of type Collection<PhiNode>
     */
    private void addAllPhi(Statement stmt, Collection<PhiNode> phiNodes) {
        for (PhiNode phiNode : phiNodes) {
            stmt.addPhi(phiNode);
        }
    }
}
