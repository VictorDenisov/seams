package org.creativelabs.ssa.visitor;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.*;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import org.creativelabs.Constants;
import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.StringCondition;
import org.creativelabs.helper.GenericVisitorHelper;
import org.creativelabs.helper.VoidVisitorHelper;
import org.creativelabs.iig.ConditionInternalInstancesGraph;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.ssa.PhiNode;
import org.creativelabs.ssa.holder.MultiHolder;
import org.creativelabs.ssa.holder.SimpleUsingModifyingVariablesHolder;
import org.creativelabs.ssa.holder.variable.StringVariable;
import org.creativelabs.ssa.holder.variable.Variable;

import java.util.*;

import static org.creativelabs.Constants.SEPARATOR;

/**
 * Uses for modification ast expressions and statements.
 */
public class SsaFormConverter extends VoidVisitorAdapter<MultiHolder> {

    private InternalInstancesGraph graph = new ConditionInternalInstancesGraph();

    private String methodName;

    private String className;

    private MethodDeclaration methodDeclaration;

    private Set<PhiNode> phiNodes = new HashSet<PhiNode>();

    public SsaFormConverter() {
    }

    public SsaFormConverter(InternalInstancesGraph graph, String className) {
        this.graph = graph;
        this.className = className;
    }

    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }

    public InternalInstancesGraph getGraph() {
        return graph;
    }

    @Override
    public void visit(ExpressionStmt n, MultiHolder arg) {
        arg.addPhiNodes(phiNodes);
        n.setExpression(getGenericExpr(n.getExpression(), arg));
    }

    @Override
    public void visit(BlockStmt n, MultiHolder arg) {
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
    public void visit(IfStmt n, MultiHolder arg) {
        //processing of the condition
        n.setCondition(getGenericExpr(n.getCondition(), arg));

        Set<Variable> assigningVariables = ((Statement) n.getThenStmt()).getVariablesHolder().getModifyingVariables();
        Set<Variable> thenCreatingVariables = ((Statement) n.getThenStmt()).getVariablesHolder().getCreatingVariables();

        //processing of the then branch
        MultiHolder thenVariables = arg.copy();
        thenVariables.setBasicBlockCondition(thenVariables.getBasicBlockCondition().and(new StringCondition(n.getCondition().toString())));

        Statement thenStmt = n.getThenStmt();

        if (!(thenStmt instanceof BlockStmt)) {
            BlockStmt blockStmt = new BlockStmt();
            blockStmt.setStmts(Arrays.asList(thenStmt));
            n.setThenStmt(blockStmt);
        }

        getVoidStmt(thenStmt, thenVariables);


        //processing of the else branch
        Statement elseStmt = n.getElseStmt();
        MultiHolder elseVariables;
        Set<Variable> elseCreatingVariables;
        if (elseStmt != null) {
            if (!(elseStmt instanceof BlockStmt)) {
                BlockStmt blockStmt = new BlockStmt();
                blockStmt.setStmts(Arrays.asList(elseStmt));
                n.setElseStmt(blockStmt);
            }
            elseVariables = thenVariables.copy();
            elseVariables.setReadVariables(arg.getReadVariables());
            elseVariables.setBasicBlockCondition(arg.getBasicBlockCondition().<Condition>copy());
            elseVariables.setBasicBlockCondition(elseVariables.getBasicBlockCondition().and((new StringCondition(n.getCondition().toString())).not()));

            getVoidStmt(elseStmt, elseVariables);
            assigningVariables.addAll(((Statement) n.getElseStmt()).getVariablesHolder().getModifyingVariables());
            elseCreatingVariables = ((Statement) n.getElseStmt()).getVariablesHolder().getCreatingVariables();
        } else {
            elseVariables = arg.copy();
            elseCreatingVariables = new TreeSet<Variable>();
        }

        //add phi for whole statement
        for (Variable name : assigningVariables) {
            if (!(thenCreatingVariables.contains(name) ^ elseCreatingVariables.contains(name))) {
                Integer[] indexes = thenVariables.getPhiIndexes(elseVariables, name);
                Integer newIndex = Math.max(indexes[0], indexes[1]) + 1;
                addPhi(n, new PhiNode(name, newIndex, PhiNode.Mode.AFTER, indexes));
                elseVariables.write(name, newIndex);
            }
        }

        arg.mergeHolders(thenVariables, elseVariables);
    }

    @Override
    public void visit(ForStmt n, MultiHolder arg) {

        Set<Variable> assigningVariables = ((Statement) n.getBody()).getVariablesHolder().getModifyingVariables();

        Set<Variable> updateVariables = ((Expression) n.getUpdate().get(0)).getVariablesHolder().getUsingVariables();
        Set<Variable> conditionVariable = ((Expression) n.getCompare()).getVariablesHolder().getUsingVariables();
        for (Variable variable : updateVariables) {
            if (conditionVariable.contains(variable)) {
                assigningVariables.add(variable);
            }
        }
        Set<Variable> creatingVariables = ((Statement) n.getBody()).getVariablesHolder().getCreatingVariables();

        MultiHolder holder1 = arg.copy();

        //process init expression
        List<Expression> expressions = n.getInit();
        if (expressions != null) {
            List<Expression> exprs = new ArrayList<Expression>();
            for (Expression expression : expressions) {
                exprs.add(getGenericExpr(expression, holder1));
            }
            n.setInit(exprs);
        }

        //phi nodes with only the one variable's index
        Set<PhiNode> beforeForPhiNodes = new TreeSet<PhiNode>();
        for (Variable var : assigningVariables) {
            if (!creatingVariables.contains(var)) {
                beforeForPhiNodes.add(new PhiNode(
                        var,
                        (holder1.read(var) == null ? 0 : holder1.read(var)) + 1,
                        PhiNode.Mode.BEFORE,
                        holder1.read(var)));
                holder1.increaseIndex(var);
            }
        }

        //process condition
        if (n.getCompare() != null) {
            n.setCompare(getGenericExpr(n.getCompare(), holder1));
        }

        MultiHolder holder2 = holder1.copy();

        //phi nodes with only one variable's index
        Set<PhiNode> inWhilePhiNodes = new TreeSet<PhiNode>();
        for (Variable var : assigningVariables) {
            if (!creatingVariables.contains(var)) {
                holder2.increaseIndex(var);
            }
        }

        //add inner phi
        for (Variable name : holder2.getDifferenceInVariables(holder1, false)) {
            inWhilePhiNodes.add(new PhiNode(name, holder2.read(name) == null ? 0 : holder2.read(name), PhiNode.Mode.BEFORE, holder1.read(name)));
        }

        for (Variable name : creatingVariables) {
            holder2.write(name, 1);
            inWhilePhiNodes.add(new PhiNode(name, holder2.read(name), PhiNode.Mode.BEFORE, 0));
        }

        //add phi to statements
        if (!inWhilePhiNodes.isEmpty()) {
            addAllPhi(((BlockStmt) n.getBody()).getStmts().get(0), inWhilePhiNodes);
        }
        if (!beforeForPhiNodes.isEmpty()) {
            addAllPhi(n, beforeForPhiNodes);
        }

        //process body
        getVoidStmt(n.getBody(), holder2);

        //process update expression
        MultiHolder holder3 = holder2.copy();
        expressions = n.getUpdate();
        if (expressions != null) {
            List<Expression> exprs = new ArrayList<Expression>();
            for (Expression expression : expressions) {
                exprs.add(getGenericExpr(expression, holder3));
            }
            n.setUpdate(exprs);
        }

        //add phi information
        for (PhiNode node : beforeForPhiNodes) {
            node.addIndex(holder3.read(node.getName()));
        }

        for (PhiNode node : inWhilePhiNodes) {
            node.addIndex(holder3.read(node.getName()));
            node.addIndexToExprStmt(holder3.read(node.getName()));
        }

        //add phi to whole statement
        for (Variable name : assigningVariables) {
            if (holder3.containsKey(name, true) && arg.containsKey(name, true)) {
                addPhi(n, new PhiNode(name, holder3.read(name) + 1, PhiNode.Mode.AFTER, holder3.getPhiIndexes(holder1, name)));
                holder3.write(name, (Math.max(holder3.read(name), holder1.read(name)) + 1));
            }
        }

        arg.mergeHolders(holder3);

    }

    @Override
    public void visit(WhileStmt n, MultiHolder arg) {
        Set<Variable> assigningVariables = ((Statement) n.getBody()).getVariablesHolder().getModifyingVariables();
        Set<Variable> creatingVariables = ((Statement) n.getBody()).getVariablesHolder().getCreatingVariables();

        MultiHolder holder = arg.copy();

        //phi nodes with only the one variable's index
        Set<PhiNode> beforeWhilePhiNodes = new TreeSet<PhiNode>();

        for (Variable var : assigningVariables) {
            if (!creatingVariables.contains(var)) {
                holder.increaseIndex(var);
                beforeWhilePhiNodes.add(new PhiNode(var, (arg.read(var) == null ? 0 : arg.read(var)) + 1,
                        PhiNode.Mode.BEFORE, arg.read(var)));
                arg.increaseIndex(var);
            }
        }

        //processing of the condition
        n.setCondition(getGenericExpr(n.getCondition(), arg));
        holder.setBasicBlockCondition(holder.getBasicBlockCondition().and(new StringCondition(n.getCondition().toString())));

        //phi nodes with only one variable's index
        Set<PhiNode> inWhilePhiNodes = new TreeSet<PhiNode>();
        for (Variable var : assigningVariables) {
            if (!creatingVariables.contains(var)) {
                holder.increaseIndex(var);
            }
        }

        //add inner phi
        for (Variable name : holder.getDifferenceInVariables(arg, false)) {
            inWhilePhiNodes.add(new PhiNode(name, holder.read(name) == null ? 0 : holder.read(name), PhiNode.Mode.BEFORE, arg.read(name)));
        }

        for (Variable name : creatingVariables) {
            holder.write(name, 1);
            inWhilePhiNodes.add(new PhiNode(name, holder.read(name), PhiNode.Mode.BEFORE, 0));
        }

        //add phi to statements
        if (!inWhilePhiNodes.isEmpty()) {
            addAllPhi(((BlockStmt) n.getBody()).getStmts().get(0), inWhilePhiNodes);
        }
        if (!beforeWhilePhiNodes.isEmpty()) {
            addAllPhi(n, beforeWhilePhiNodes);
        }

        //process body
        getVoidStmt(n.getBody(), holder);

        //add phi information
        for (PhiNode node : beforeWhilePhiNodes) {
            node.addIndex(holder.read(node.getName()));
        }

        for (PhiNode node : inWhilePhiNodes) {
            node.addIndex(holder.read(node.getName()));
            node.addIndexToExprStmt(holder.read(node.getName()));
        }

        //add phi to whole statement
        for (Variable name : assigningVariables) {
            if (holder.containsKey(name, true) && arg.containsKey(name, true)) {
                addPhi(n, new PhiNode(name, holder.read(name) + 1, PhiNode.Mode.AFTER, holder.getPhiIndexes(arg, name)));
                arg.write(name, (Math.max(holder.read(name), arg.read(name)) + 1));
            }
        }

        arg.mergeHolders(holder);
    }

    @Override
    public void visit(ForeachStmt n, MultiHolder arg) {

        Set<Variable> assigningVariables = ((Statement) n.getBody()).getVariablesHolder().getModifyingVariables();
        Set<Variable> conditionsVars = ((Expression) n.getVariable()).getVariablesHolder().getUsingVariables();
        Set<Variable> creatingVariables = ((Statement) n.getBody()).getVariablesHolder().getCreatingVariables();

        MultiHolder holder = arg.copy();

        //modify foreach iterable expression
        n.setIterable(getGenericExpr(n.getIterable(), arg));
        n.setVariable((VariableDeclarationExpr) getGenericExpr(n.getVariable(), holder));
        n.getVariable().getVars().get(0).setInit(new MethodCallExpr(n.getIterable(), "#next"));

        //create block condition
        holder.setBasicBlockCondition(holder.getBasicBlockCondition().and(new StringCondition(n.getVariable().toString() +
                ":" + n.getIterable().toString())));

        //phi nodes with only one variable's index
        Set<PhiNode> inForPhiNodes = new HashSet<PhiNode>();
        for (Variable var : assigningVariables) {
            if (!conditionsVars.contains(var) && !creatingVariables.contains(var)) {
                holder.increaseIndex(var);
            }
        }
        //create inner phi
        for (Variable name : assigningVariables) {
            if (!conditionsVars.contains(name) && !creatingVariables.contains(name)) {
                inForPhiNodes.add(new PhiNode(name, holder.read(name) == null ? 0 : holder.read(name), PhiNode.Mode.BEFORE, arg.read(name)));
            }
        }

        for (Variable name : creatingVariables) {
            holder.write(name, 1);
            inForPhiNodes.add(new PhiNode(name, holder.read(name), PhiNode.Mode.BEFORE, 0));
        }

        //add inner phi and curly bracket if they are missed
        //and process
        if (n.getBody() instanceof BlockStmt) {
            if (((BlockStmt) n.getBody()).getStmts() != null) {
                addAllPhi(((BlockStmt) n.getBody()).getStmts().get(0), inForPhiNodes);
                getVoidStmt(n.getBody(), holder);
            }
        } else {
            BlockStmt blockStmt = new BlockStmt();
            blockStmt.setStmts(Arrays.asList(n.getBody()));
            n.setBody(blockStmt);
            addAllPhi(n.getBody(), inForPhiNodes);
            getVoidStmt(n.getBody(), holder);
        }

        //add information to phi nodes
        for (PhiNode node : inForPhiNodes) {
            node.addIndex(holder.read(node.getName()));
            node.addIndexToExprStmt(holder.read(node.getName()));
        }

        //add phi after whole statement
        for (Variable name : assigningVariables) {
            if (holder.containsKey(name, true) && arg.containsKey(name, true)) {
                addPhi(n, new PhiNode(name, holder.read(name) + 1, PhiNode.Mode.AFTER, holder.getPhiIndexes(arg, name)));
                arg.write(name, (Math.max(holder.read(name), arg.read(name)) + 1));
            }
        }
    }

    @Override
    public void visit(ReturnStmt n, MultiHolder arg) {
        if (n.getExpr() != null) {
            n.setExpr(getGenericExpr(n.getExpr(), arg));
        }
    }

    @Override
    public void visit(TryStmt n, MultiHolder arg) {
        //process try block
        getVoidStmt(n.getTryBlock(), arg);

        //process catches blocks
        if (n.getCatchs() != null) {
            for (CatchClause catchClause : n.getCatchs()) {
                MultiHolder holder = arg.copy();
                String name = catchClause.getExcept().getId().getName();
                holder.addArgName(name);
                Variable variable = new StringVariable(name, Constants.ARG_SCOPE);

                holder.write(variable, 0);
                catchClause.getExcept().getId().setName(name + SEPARATOR + 0);

                getVoidStmt(catchClause.getCatchBlock(), holder);
            }
        }

        //process finally block
        if (n.getFinallyBlock() != null) {
            getVoidStmt(n.getFinallyBlock(), arg);
        }
    }

    @Override
    public void visit(MethodDeclaration n, MultiHolder arg) {
//        methodDeclaration = new CopyingUtils<MethodDeclaration>().copy(n);
        methodDeclaration = n;

        //generate method name
        StringBuilder types = new StringBuilder("(");
        if (methodDeclaration.getParameters() != null) {
            for (Parameter parameter : methodDeclaration.getParameters()) {
                types.append(parameter.getType().toString());
                types.append(", ");
            }
        }
        types.append(")");
        methodName = methodDeclaration.getName() + types.toString();

        Map<Variable, Integer> variables = new TreeMap<Variable, Integer>();
        if (methodDeclaration.getParameters() != null) {
            for (Parameter parameter : methodDeclaration.getParameters()) {
//                graph.addVertexConditions(methodName + SEPARATOR + parameter.getId().getName() + SEPARATOR + 0,
//                        new FalseBooleanCondition(),
//                        new TrueBooleanCondition());

                //add arguments to arguments holder
                arg.getMethodArgsHolder().addArgName(parameter.getId().getName());
                //add arguments to variables holder
                variables.put(new StringVariable(parameter.getId().getName(), Constants.ARG_SCOPE), 0);
                //convert to ssa
                parameter.getId().setName(parameter.getId().getName() + SEPARATOR + 0);
            }
        }

        if (methodDeclaration.getBody() != null) {
            //find of using and modifying variables
            new UsingModifyingVariablesVisitor(arg.getMethodArgsHolder()).visit(methodDeclaration.getBody(), new SimpleUsingModifyingVariablesHolder());
        }

        for (String fieldName : arg.getFieldsHolder().getFieldsNames()) {
//                graph.addVertexConditions(methodName + SEPARATOR + fieldName + SEPARATOR + 0,
//                        new FalseBooleanCondition(),
//                        new TrueBooleanCondition());
//
//                graph.addVertexConditions(fieldName + SEPARATOR + 0,
//                        new FalseBooleanCondition(),
//                        new TrueBooleanCondition());
//
//                graph.addEdge(methodName + SEPARATOR + fieldName + SEPARATOR + 0,
//                        fieldName + SEPARATOR + 0);

            //add fields to variables holder
            variables.put(new StringVariable(fieldName, Constants.THIS_SCOPE), 0);
        }

        //add read and write variables
        Map<Variable, Integer> vars = variables;
        vars.putAll(arg.getReadVariables());
        arg.setReadVariables(vars);

        vars = new HashMap<Variable, Integer>(variables);
        vars.putAll(arg.getWriteVariables());
        arg.setWriteVariables(vars);

        visit(methodDeclaration.getBody(), arg);
    }

    /**
     * Modifies the statement.
     *
     * @param statement of type Statement
     * @param arg       of type MultiHolder
     */
    private void getVoidStmt(Statement statement, MultiHolder arg) {
        VoidVisitorHelper.visitStatement(statement, arg, this);
    }

    /**
     * Modifies the expression.
     *
     * @param expression of type Expression
     * @param arg        of type MultiHolder
     * @return Expression
     */
    private Expression getGenericExpr(Expression expression, MultiHolder arg) {
        return GenericVisitorHelper.visitExpression(expression, arg, new ExpressionStmtVisitor(graph, methodName, className));
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
        phiNodes.add(phiNode);
//        graph.addVertexConditions(methodName + Constants.SEPARATOR +
//                phiNode.getName().getString() + Constants.SEPARATOR + phiNode.getLeftIndex(),
//                graph.getInternalVertexCondition(methodName + Constants.SEPARATOR +
//                        phiNode.getName().getString() + Constants.SEPARATOR + phiNode.getIndexes()[0]),
//                graph.getExternalVertexCondition(methodName + Constants.SEPARATOR +
//                        phiNode.getName().getString() + Constants.SEPARATOR + phiNode.getIndexes()[0])
//        );
//        graph.addEdge(methodName + Constants.SEPARATOR +
//                phiNode.getName().getString() + Constants.SEPARATOR + phiNode.getLeftIndex(),
//                methodName + Constants.SEPARATOR +
//                        phiNode.getName().getString() + Constants.SEPARATOR + phiNode.getIndexes()[0]);
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
            this.phiNodes.add(phiNode);
//            graph.addVertexConditions(methodName + Constants.SEPARATOR +
//                    phiNode.getName().getString() + Constants.SEPARATOR + phiNode.getLeftIndex(),
//                    graph.getInternalVertexCondition(methodName + Constants.SEPARATOR +
//                            phiNode.getName().getString() + Constants.SEPARATOR + phiNode.getIndexes()[0]),
//                    graph.getExternalVertexCondition(methodName + Constants.SEPARATOR +
//                            phiNode.getName().getString() + Constants.SEPARATOR + phiNode.getIndexes()[0])
//            );
//            graph.addEdge(methodName + Constants.SEPARATOR +
//                    phiNode.getName().getString() + Constants.SEPARATOR + phiNode.getLeftIndex(),
//                    methodName + Constants.SEPARATOR +
//                            phiNode.getName().getString() + Constants.SEPARATOR + phiNode.getIndexes()[0]);
        }
    }
}
