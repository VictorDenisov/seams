package org.creativelabs.ssa;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.*;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.EmptyCondition;
import org.creativelabs.graph.condition.StringCondition;
import org.creativelabs.graph.condition.bool.FalseBooleanCondition;
import org.creativelabs.graph.condition.bool.TrueBooleanCondition;
import org.creativelabs.iig.ConditionInternalInstancesGraph;
import org.creativelabs.iig.InternalInstancesGraph;

import java.util.*;

/**
 * Uses for modification ast expressions and statements.
 */
public class SsaFormConverter extends VoidVisitorAdapter<VariablesHolder> {

    public static final String SEPARATOR = "#";

    private InternalInstancesGraph graph = new ConditionInternalInstancesGraph();

    private String methodName;

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

    @Override
    public void visit(ExpressionStmt n, VariablesHolder arg) {
        n.setExpression(getGenericExpr(n.getExpression(), arg));
    }

    @Override
    public void visit(BlockStmt n, VariablesHolder arg) {
        if (n != null && n.getStmts() != null) {
            List<Statement> statements = new ArrayList<Statement>();
            for (Statement statement : n.getStmts()) {

                //changing statement
                getVoidStmt(statement, arg);

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
    public void visit(IfStmt n, VariablesHolder arg) {
        //processing of the condition
        n.setCondition(getGenericExpr(n.getCondition(), arg));

        Set<String> assigningVariables = ((Statement) n.getThenStmt()).getVariablesHolder().getModifyingVariables();

        //processing of the then branch
        VariablesHolder thenVariables = arg.copy();
        thenVariables.setCondition(thenVariables.getCondition().and(new StringCondition(n.getCondition().toString())));

        Statement thenStmt = n.getThenStmt();
        getVoidStmt(thenStmt, thenVariables);


        //processing of the else branch
        Statement elseStmt = n.getElseStmt();
        VariablesHolder elseVariables;
        if (elseStmt != null) {
            elseVariables = thenVariables.copy();
            elseVariables.setReadVariables(arg.getReadVariables());
            elseVariables.setCondition(arg.getCondition().copy());
            elseVariables.setCondition(elseVariables.getCondition().and((new StringCondition(n.getCondition().toString())).not()));

            getVoidStmt(elseStmt, elseVariables);
            assigningVariables.addAll(((Statement) n.getElseStmt()).getVariablesHolder().getModifyingVariables());
        } else {
            elseVariables = arg.copy();
        }

        for (String name : assigningVariables) {
            Integer newIndex = Math.max(thenVariables.read(name) == null ? -1 : thenVariables.read(name), elseVariables.read(name) == null ? -1 : elseVariables.read(name)) + 1;

            String thisString;
            if (thenVariables.containField(name)) {
                thisString = "this.";
            } else {
                thisString = "";
            }

            addPhi(n, new PhiNode(thisString + name, newIndex, PhiNode.Mode.AFTER, elseVariables.getPhiIndexes(thenVariables, name)));
            elseVariables.write(name, newIndex);
        }

        arg.mergeHolders(thenVariables, elseVariables);
    }

    @Override
    public void visit(ForStmt n, VariablesHolder arg) {
        //TODO
        VariablesHolder holder1 = arg.copy();
        List<Expression> expressions = n.getInit();
        if (expressions != null) {
            List<Expression> exprs = new ArrayList<Expression>();
            for (Expression expression : expressions) {
                exprs.add(getGenericExpr(expression, holder1));
            }
            n.setInit(exprs);
        }

        VariablesHolder holder2 = holder1.copy();

        expressions = n.getUpdate();
        if (expressions != null) {
            Set<String> usingVariables = expressions.get(0).getVariablesHolder().getUsingVariables();

            for (String var : usingVariables) {
                holder2.increaseIndex(var);
            }

            List<Expression> exprs = new ArrayList<Expression>();
            for (Expression expression : expressions) {
                exprs.add(getGenericExpr(expression, holder2));
            }
            n.setUpdate(exprs);
        }

        VariablesHolder holder3 = holder2.copy();

//        for (String name : holder3.getDifferenceInVariables(holder1, false)) {
////            preparePhi(name + SEPARATOR + (holder1.read(name) + 1), false, holder3.getPhi(holder1, name));
//        }

        getVoidStmt(n.getBody(), holder3);

        if (n.getCompare() != null) {
            n.setCompare(getGenericExpr(n.getCompare(), holder1));
        }
//        List<Expression> phisInCondition = new ArrayList<Expression>();
////        String[] s = holder1.getPhi(holder2, "i");
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
    }

    @Override
    public void visit(WhileStmt n, VariablesHolder arg) {
        Set<String> assigningVariables = ((Statement) n.getBody()).getVariablesHolder().getModifyingVariables();

        VariablesHolder holder = arg.copy();
        holder.setCondition(holder.getCondition().and(new StringCondition(n.getCondition().toString())));

        //phi nodes with only the one variable's index
        Set<PhiNode> beforeWhilePhiNodes = new HashSet<PhiNode>();

        for (String var : assigningVariables) {
            holder.increaseIndex(var);
            if (assigningVariables.contains(var)) {
                String thisString;
                if (holder.containField(var)) {
                    thisString = "this.";
                } else {
                    thisString = "";
                }
                if (arg.read(var) != null) {
                    beforeWhilePhiNodes.add(new PhiNode(thisString + var, (arg.read(var) == null ? 0 : arg.read(var)) + 1,
                            PhiNode.Mode.BEFORE, arg.read(var)));
                } else {
                    beforeWhilePhiNodes.add(new PhiNode(thisString + var, (arg.read(var) == null ? 0 : arg.read(var)) + 1,
                            PhiNode.Mode.BEFORE));
                }
                arg.increaseIndex(var);
            }
        }

        //processing of the condition
        n.setCondition(getGenericExpr(n.getCondition(), arg));


        for (String var : assigningVariables) {
            if (assigningVariables.contains(var)) {
                holder.increaseIndex(var);
            }
        }

        //phi nodes with only one variable's index
        Set<PhiNode> inWhilePhiNodes = new HashSet<PhiNode>();

        for (String name : holder.getDifferenceInVariables(arg, false)) {
            String thisString;
            if (holder.containField(name)) {
                thisString = "this.";
            } else {
                thisString = "";
            }
            if (arg.read(name) != null) {
                inWhilePhiNodes.add(new PhiNode(thisString + name, holder.read(name) == null ? 0 : holder.read(name), PhiNode.Mode.BEFORE, arg.read(name)));
            } else {
                inWhilePhiNodes.add(new PhiNode(thisString + name, holder.read(name) == null ? 0 : holder.read(name), PhiNode.Mode.BEFORE));
            }
        }

        if (!inWhilePhiNodes.isEmpty()) {
            addAllPhi(((BlockStmt) n.getBody()).getStmts().get(0), inWhilePhiNodes);
        }
        if (!beforeWhilePhiNodes.isEmpty()) {
            addAllPhi(n, beforeWhilePhiNodes);
        }

        getVoidStmt(n.getBody(), holder);

        for (PhiNode node : beforeWhilePhiNodes) {
            node.addIndex(holder.read(node.getName()));
        }

        for (PhiNode node : inWhilePhiNodes) {
            node.addIndex(holder.read(node.getName()));
            node.addIndexToExprStmt(holder.read(node.getName()));
        }

        for (String name : assigningVariables) {
            if (holder.containsKey(name, true) && arg.containsKey(name, true)) {

                String thisString;
                if (holder.containField(name)) {
                    thisString = "this.";
                } else {
                    thisString = "";
                }

                addPhi(n, new PhiNode(thisString + name, holder.read(name) + 1, PhiNode.Mode.AFTER, holder.getPhiIndexes(arg, name)));
                arg.write(name, (Math.max(holder.read(name), arg.read(name)) + 1));
            }
        }

        arg.mergeHolders(holder);
    }

    @Override
    public void visit(ForeachStmt n, VariablesHolder arg) {

        Set<String> assigningVariables = ((Statement) n.getBody()).getVariablesHolder().getModifyingVariables();

        Set<String> conditionsVars = ((Expression) n.getVariable()).getVariablesHolder().getUsingVariables();

        VariablesHolder holder = arg.copy();

        n.setIterable(getGenericExpr(n.getIterable(), arg));
        n.setVariable((VariableDeclarationExpr) getGenericExpr(n.getVariable(), holder));

        n.getVariable().getVars().get(0).setInit(new MethodCallExpr(n.getIterable(), "#next"));

        holder.setCondition(holder.getCondition().and(new StringCondition(n.getVariable().toString() +
                ":" + n.getIterable().toString())));


        for (String var : assigningVariables) {
            if (!conditionsVars.contains(var)) {
                holder.increaseIndex(var);
            }
        }

        //phi nodes with only one variable's index
        Set<PhiNode> inWhilePhiNodes = new HashSet<PhiNode>();

        for (String name : assigningVariables) {
            String thisString;
            if (holder.containField(name)) {
                thisString = "this.";
            } else {
                thisString = "";
            }
            if (!conditionsVars.contains(name)) {
                if (arg.read(name) != null) {
                    inWhilePhiNodes.add(new PhiNode(thisString + name, holder.read(name) == null ? 0 : holder.read(name), PhiNode.Mode.BEFORE, arg.read(name)));
                } else {
                    inWhilePhiNodes.add(new PhiNode(thisString + name, holder.read(name) == null ? 0 : holder.read(name), PhiNode.Mode.BEFORE));
                }
            }
        }

        if (n.getBody() instanceof BlockStmt) {
            if (((BlockStmt) n.getBody()).getStmts() != null) {
                addAllPhi(((BlockStmt) n.getBody()).getStmts().get(0), inWhilePhiNodes);
                getVoidStmt(n.getBody(), holder);
            }
        } else if (n.getBody() instanceof IfStmt) {
            BlockStmt blockStmt = new BlockStmt();
            blockStmt.setStmts(Arrays.asList(n.getBody()));
            n.setBody(blockStmt);

            addAllPhi((BlockStmt) n.getBody(), inWhilePhiNodes);
            getVoidStmt(n.getBody(), holder);
        } else {
            //no operations
        }

        for (PhiNode node : inWhilePhiNodes) {
            if (holder.read(node.getName()) != null) {
                node.addIndex(holder.read(node.getName()));
                node.addIndexToExprStmt(holder.read(node.getName()));
            }
        }

        for (String name : assigningVariables) {
            if (holder.containsKey(name, true) && arg.containsKey(name, true)) {

                String thisString;
                if (holder.containField(name)) {
                    thisString = "this.";
                } else {
                    thisString = "";
                }

                addPhi(n, new PhiNode(thisString + name, holder.read(name) + 1, PhiNode.Mode.AFTER, holder.getPhiIndexes(arg, name)));
                arg.write(name, (Math.max(holder.read(name), arg.read(name)) + 1));
            }
        }
    }

    @Override
    public void visit(ReturnStmt n, VariablesHolder arg) {
        if (n.getExpr() != null) {
            n.setExpr(getGenericExpr(n.getExpr(), arg));
        }
    }

    @Override
    public void visit(TryStmt n, VariablesHolder arg) {
        getVoidStmt(n.getTryBlock(), arg);

        if (n.getCatchs() != null) {
            for (CatchClause catchClause : n.getCatchs()) {
                VariablesHolder holder = arg.copy();
                holder.write(catchClause.getExcept().getId().getName(), 0);

                String name = catchClause.getExcept().getId().getName();
                holder.write(name, 0);
                catchClause.getExcept().getId().setName(name + SEPARATOR + 0);

                holder.increaseIndex(catchClause.getExcept().getId().getName());
                getVoidStmt(catchClause.getCatchBlock(), holder);
            }
        }

        if (n.getFinallyBlock() != null) {
            getVoidStmt(n.getFinallyBlock(), arg);
        }
    }

    @Override
    public void visit(MethodDeclaration n, VariablesHolder arg) {
//        methodDeclaration = new CopyingUtils<MethodDeclaration>().copy(n);
        methodDeclaration = n;
        StringBuilder types = new StringBuilder("(");
        if (methodDeclaration.getParameters() != null) {
            for (Parameter parameter : methodDeclaration.getParameters()) {
                types.append(parameter.getType().toString());
                types.append(", ");
            }
        }
        types.append(")");

        methodName = methodDeclaration.getName() + types.toString();

        new UsingModifyingVariablesVisitor().visit(methodDeclaration, new UMVariablesHolder());
        Condition condition = new EmptyCondition();

        Map<String, Integer> map = new HashMap<String, Integer>();

        if (methodDeclaration.getParameters() != null) {
            for (Parameter parameter : methodDeclaration.getParameters()) {

                String target = methodName + SEPARATOR + parameter.getId().getName();
                String value = parameter.getId().getName();

                graph.addVertexConditions(value + SEPARATOR + 0,
                        new FalseBooleanCondition(),
                        new TrueBooleanCondition());

                graph.addVertexConditions(target + SEPARATOR + 0,
                        new FalseBooleanCondition(),
                        new TrueBooleanCondition());

                graph.addEdge(target + SEPARATOR + 0,
                        value + SEPARATOR + 0);

                //TODO may be need to add
                map.put(parameter.getId().getName(), 0);

                parameter.getId().setName(parameter.getId().getName() + SEPARATOR + 0);
            }
        }
        VariablesHolder holder = new VariablesHolder(map, condition);

        if (arg != null) {
            holder.mergeHolders(arg);
            holder.setFieldsNames(arg.getFieldsNames());
        }

        visit(methodDeclaration.getBody(), holder);
        System.out.println(methodDeclaration);
    }

    /**
     * Modifies the statement.
     *
     * @param statement of type Statement
     * @param arg       of type VariablesHolder
     */
    private void getVoidStmt(Statement statement, VariablesHolder arg) {
        VoidVisitorHelper.visitStatement(statement, arg, this);
    }

    /**
     * Modifies the expression.
     *
     * @param expression of type Expression
     * @param arg        of type VariablesHolder
     */
    private void getVoidExpr(Expression expression, VariablesHolder arg) {
        VoidVisitorHelper.<VariablesHolder>visitExpression(expression, arg, this);
    }

    /**
     * Modifies the expression.
     *
     * @param expression of type Expression
     * @param arg        of type VariablesHolder
     * @return Expression
     */
    private Expression getGenericExpr(Expression expression, VariablesHolder arg) {
        return GenericVisitorHelper.<Expression, VariablesHolder>visitExpression(expression, arg, new ExpressionStmtVisitor(graph, methodName));
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
