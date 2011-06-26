package org.creativelabs.ssa.visitor;

import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.creativelabs.Constants;
import org.creativelabs.copy.CopyingUtils;
import org.creativelabs.helper.VoidVisitorHelper;
import org.creativelabs.ssa.holder.MethodArgsHolder;
import org.creativelabs.ssa.holder.SimpleMethodArgsHolder;
import org.creativelabs.ssa.holder.SimpleUsingModifyingVariablesHolder;
import org.creativelabs.ssa.holder.UsingModifyingVariablesHolder;
import org.creativelabs.ssa.holder.variable.StringVariable;
import org.creativelabs.ssa.holder.variable.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Stores using and modifying variables in statements to statements.
 *
 * @author azotcsit
 *         Date: 23.04.11
 *         Time: 20:44
 */
public class UsingModifyingVariablesVisitor extends VoidVisitorAdapter<UsingModifyingVariablesHolder> {

    private Log log = LogFactory.getLog(UsingModifyingVariablesVisitor.class);

    MethodArgsHolder methodArgsHolder = new SimpleMethodArgsHolder();
    Set<String> createdVars = new TreeSet<String>();

    private boolean isCondition = false;
    private boolean isObjectCreation = false;

    public UsingModifyingVariablesVisitor(MethodArgsHolder methodArgsHolder) {
        this.methodArgsHolder = methodArgsHolder;
    }

    @Override
    public void visit(NameExpr n, UsingModifyingVariablesHolder arg) {
        arg.addUsingVariable(createVariable(getName(n.getName()), getScope(n.getName()), methodArgsHolder));
        if (isCondition) {
            ((Expression) n).getVariablesHolder().add(arg);
        }
    }

    @Override
    public void visit(FieldAccessExpr n, UsingModifyingVariablesHolder arg) {
        VoidVisitorHelper.visitExpression(n.getScope(), arg, this);
        arg.addUsingVariable(createVariable(n.getField(), n.getScope().toString(), methodArgsHolder));
    }

    @Override
    public void visit(VariableDeclaratorId n, UsingModifyingVariablesHolder arg) {
        String name = n.getName();
        createdVars.add(name);
            arg.addUsingVariable(createVariable(name, Constants.EMPTY_SCOPE, methodArgsHolder));
            if (!isCondition) {
                arg.addModifyingVariable(createVariable(name, Constants.EMPTY_SCOPE, methodArgsHolder));
            }
    }

    @Override
    public void visit(VariableDeclarationExpr n, UsingModifyingVariablesHolder arg) {
        boolean condition = isCondition;
        isCondition = true;
        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        super.visit(n, holder);
        ((Expression) n).getVariablesHolder().add(holder);
        arg.add(holder);
        isCondition = condition;
    }

    @Override
    public void visit(AssignExpr n, UsingModifyingVariablesHolder arg) {
        SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
        VoidVisitorHelper.visitExpression(n.getTarget(), holder, this);
        holder.addModifyingVariables(holder.getUsingVariables());
        ((Expression) n).getVariablesHolder().add(holder);
        arg.add(holder);

        VoidVisitorHelper.visitExpression(n.getValue(), arg, this);
    }

    @Override
    public void visit(UnaryExpr n, UsingModifyingVariablesHolder arg) {
        if (isCondition) {
            SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
            super.visit(n, holder);
            ((Expression) n).getVariablesHolder().add(holder);
            arg.add(holder);
        } else {
            super.visit(n, arg);
        }
    }

    @Override
    public void visit(BinaryExpr n, UsingModifyingVariablesHolder arg) {
        if (isCondition) {
            SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
            super.visit(n, holder);
            ((Expression) n).getVariablesHolder().add(holder);
            arg.add(holder);
        } else {
            super.visit(n, arg);
        }
    }

    @Override
    public void visit(MethodCallExpr n, UsingModifyingVariablesHolder arg) {
        if (isCondition) {
            SimpleUsingModifyingVariablesHolder holder = new SimpleUsingModifyingVariablesHolder();
            super.visit(n, holder);
            ((Expression) n).getVariablesHolder().add(holder);
            arg.add(holder);
        } else {
            super.visit(n, arg);
        }
    }

    @Override
    public void visit(BlockStmt body, UsingModifyingVariablesHolder arg) {
        List<Statement> statements = body.getStmts();
        if (statements != null) {
            List<UsingModifyingVariablesHolder> holders = new ArrayList<UsingModifyingVariablesHolder>();
            for (Statement statement : statements) {
                SimpleUsingModifyingVariablesHolder holder = CopyingUtils.copy(arg);
                VoidVisitorHelper.visitStatement(statement, holder, this);
                holders.add(holder);
            }
            for (UsingModifyingVariablesHolder holder : holders) {
                arg.add(holder);
            }
        }
        ((Statement) body).getVariablesHolder().add(arg);
    }

    @Override
    public void visit(IfStmt n, UsingModifyingVariablesHolder arg) {
        SimpleUsingModifyingVariablesHolder conditionHolder = new SimpleUsingModifyingVariablesHolder();
        isCondition = true;
        VoidVisitorHelper.visitExpression(n.getCondition(), conditionHolder, this);
        isCondition = false;

        SimpleUsingModifyingVariablesHolder thenHolder = new SimpleUsingModifyingVariablesHolder();
        VoidVisitorHelper.visitStatement(n.getThenStmt(), thenHolder, this);

        if (n.getElseStmt() != null) {
            SimpleUsingModifyingVariablesHolder elseHolder = new SimpleUsingModifyingVariablesHolder();
            VoidVisitorHelper.visitStatement(n.getElseStmt(), elseHolder, this);
            arg.add(elseHolder);
        }

        arg.add(conditionHolder);
        arg.add(thenHolder);
    }

    @Override
    public void visit(WhileStmt n, UsingModifyingVariablesHolder arg) {
        SimpleUsingModifyingVariablesHolder conditionHolder = new SimpleUsingModifyingVariablesHolder();
        isCondition = true;
        VoidVisitorHelper.visitExpression(n.getCondition(), conditionHolder, this);
        isCondition = false;

        SimpleUsingModifyingVariablesHolder bodyHolder = new SimpleUsingModifyingVariablesHolder();
        VoidVisitorHelper.visitStatement(n.getBody(), bodyHolder, this);

        arg.add(conditionHolder);
        arg.add(bodyHolder);
    }

    @Override
    public void visit(ForeachStmt n, UsingModifyingVariablesHolder arg) {
        SimpleUsingModifyingVariablesHolder conditionHolder = new SimpleUsingModifyingVariablesHolder();
        isCondition = true;
        VoidVisitorHelper.visitExpression(n.getVariable(), conditionHolder, this);
        isCondition = false;

        SimpleUsingModifyingVariablesHolder bodyHolder = new SimpleUsingModifyingVariablesHolder();
        VoidVisitorHelper.visitStatement(n.getBody(), bodyHolder, this);

        arg.add(conditionHolder);
        arg.add(bodyHolder);
    }

    @Override
    public void visit(ForStmt n, UsingModifyingVariablesHolder arg) {
        SimpleUsingModifyingVariablesHolder initHolder = null;
        if (n.getInit() != null) {
            initHolder = new SimpleUsingModifyingVariablesHolder();
            isCondition = true;
            //only one init expression is available by style
            VoidVisitorHelper.visitExpression(n.getInit().get(0), initHolder, this);
            isCondition = false;
        }

        SimpleUsingModifyingVariablesHolder conditionHolder = null;
        if (n.getCompare() != null) {
            conditionHolder = new SimpleUsingModifyingVariablesHolder();
            isCondition = true;
            VoidVisitorHelper.visitExpression(n.getCompare(), conditionHolder, this);
            isCondition = false;
        }

        SimpleUsingModifyingVariablesHolder updateHolder = null;
        if (n.getUpdate() != null) {
            updateHolder = new SimpleUsingModifyingVariablesHolder();
            isCondition = true;
            //only one update expression is available by style
            VoidVisitorHelper.visitExpression(n.getUpdate().get(0), updateHolder, this);
            isCondition = false;
        }

        SimpleUsingModifyingVariablesHolder bodyHolder = new SimpleUsingModifyingVariablesHolder();
        VoidVisitorHelper.visitStatement(n.getBody(), bodyHolder, this);

        if (initHolder != null) {
            arg.add(initHolder);
        }
        if (conditionHolder != null) {
            arg.add(conditionHolder);
        }
//        if (updateHolder != null) {
//            arg.add(updateHolder);
//        }
        arg.add(bodyHolder);
    }

    @Override
    public void visit(TryStmt n, UsingModifyingVariablesHolder arg) {
        VoidVisitorHelper.visitStatement(n.getTryBlock(), arg, this);
        if (n.getFinallyBlock() != null) {
            VoidVisitorHelper.visitStatement(n.getFinallyBlock(), arg, this);
        }
    }

    private Variable createVariable(String name, String scope, MethodArgsHolder methodArgsHolder) {
        if (scope == null || scope.isEmpty()) {
            if (methodArgsHolder.containsArgName(name)) {
                return new StringVariable(name, Constants.ARG_SCOPE);
            } else {
                if (createdVars.contains(name)) {
                    return new StringVariable(name, Constants.EMPTY_SCOPE);
                }
                return new StringVariable(name, Constants.THIS_SCOPE);
            }
        } else {
            return new StringVariable(name, scope);
        }
    }

    private String getScope(String s) {
        int i = s.lastIndexOf(".");
        if (i == -1) {
            return null;
        } else {
            if (s.indexOf(".") != i) {
                log.warn("Scope is very complex [scope=" + s + "]");
            }
            return s.substring(i, s.length());
        }
    }

    private String getName(String s) {
        int i = s.lastIndexOf(".");
        if (i == -1) {
            return s;
        } else {
            return s.substring(i, s.length());
        }
    }

}
