package org.creativelabs.ssa.visitor;

import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import org.creativelabs.copy.CopyingUtils;
import org.creativelabs.helper.VoidVisitorHelper;
import org.creativelabs.ssa.holder.SimpleUsingModifyingVariablesHolder;
import org.creativelabs.ssa.holder.UsingModifyingVariablesHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores using and modifying variables in statements to statements.
 *
 * @author azotcsit
 *         Date: 23.04.11
 *         Time: 20:44
 */
public class UsingModifyingVariablesVisitor extends VoidVisitorAdapter<UsingModifyingVariablesHolder> {
    //TODO implement all functionality

    private boolean isCondition = false;

    @Override
    public void visit(NameExpr n, UsingModifyingVariablesHolder arg) {
        arg.addUsingVariable(n.getName());
        if (isCondition) {
            ((Expression) n).getVariablesHolder().add(arg);
        }
    }

    @Override
    public void visit(FieldAccessExpr n, UsingModifyingVariablesHolder arg) {
        VoidVisitorHelper.visitExpression(n.getScope(), arg, this);
        arg.addUsingVariable(n.getField());
    }

    @Override
    public void visit(VariableDeclaratorId n, UsingModifyingVariablesHolder arg) {
        arg.addUsingVariable(n.getName());
        if (!isCondition) {
            arg.addModifyingVariable(n.getName());
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
        if (updateHolder != null) {
            arg.add(updateHolder);
        }
        arg.add(bodyHolder);
    }

}
