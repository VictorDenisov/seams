package org.creativelabs.ssa;

import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores using and modifying variables in statements to statements.
 *
 * @author azotcsit
 *         Date: 23.04.11
 *         Time: 20:44
 */
public class UsingModifyingVariablesVisitor extends VoidVisitorAdapter<UMVariablesHolder> {
    //TODO implement all functionality

    private boolean isCondition = false;

    @Override
    public void visit(NameExpr n, UMVariablesHolder arg) {
        arg.addUsingVariable(n.getName());
        if (isCondition) {
            ((Expression) n).getVariablesHolder().add(arg);
        }
    }

    @Override
    public void visit(FieldAccessExpr n, UMVariablesHolder arg) {
        VoidVisitorHelper.<UMVariablesHolder>visitExpression(n.getScope(), arg, this);
        arg.addUsingVariable(n.getField());
    }

    @Override
    public void visit(VariableDeclaratorId n, UMVariablesHolder arg) {
        arg.addUsingVariable(n.getName());
        if (!isCondition) {
            arg.addModifyingVariable(n.getName());
        }
    }

    @Override
    public void visit(VariableDeclarationExpr n, UMVariablesHolder arg) {
        boolean condition = isCondition;
        isCondition = true;
        UMVariablesHolder holder = new UMVariablesHolder();
        super.visit(n, holder);
        ((Expression) n).getVariablesHolder().add(holder);
        arg.add(holder);
        isCondition = condition;
    }

    @Override
    public void visit(AssignExpr n, UMVariablesHolder arg) {
        UMVariablesHolder holder = new UMVariablesHolder();
        VoidVisitorHelper.<UMVariablesHolder>visitExpression(n.getTarget(), holder, this);
        holder.addModifyingVariables(holder.getUsingVariables());
        ((Expression) n).getVariablesHolder().setCopy(holder);
        arg.add(holder);

        VoidVisitorHelper.<UMVariablesHolder>visitExpression(n.getValue(), arg, this);
    }

    @Override
    public void visit(UnaryExpr n, UMVariablesHolder arg) {
        if (isCondition) {
            UMVariablesHolder holder = new UMVariablesHolder();
            super.visit(n, holder);
            ((Expression) n).getVariablesHolder().add(holder);
            arg.add(holder);
        } else {
            super.visit(n, arg);
        }
    }

    @Override
    public void visit(BinaryExpr n, UMVariablesHolder arg) {
        if (isCondition) {
            UMVariablesHolder holder = new UMVariablesHolder();
            super.visit(n, holder);
            ((Expression) n).getVariablesHolder().add(holder);
            arg.add(holder);
        } else {
            super.visit(n, arg);
        }
    }

    @Override
    public void visit(MethodCallExpr n, UMVariablesHolder arg) {
        if (isCondition) {
            UMVariablesHolder holder = new UMVariablesHolder();
            super.visit(n, holder);
            ((Expression) n).getVariablesHolder().add(holder);
            arg.add(holder);
        } else {
            super.visit(n, arg);
        }
    }

    @Override
    public void visit(BlockStmt body, UMVariablesHolder arg) {
        List<Statement> statements = body.getStmts();
        if (statements != null) {
            List<UMVariablesHolder> holders = new ArrayList<UMVariablesHolder>();
            for (Statement statement : statements) {
                UMVariablesHolder holder = new CopyingUtils<UMVariablesHolder>().copy(arg);
                VoidVisitorHelper.<UMVariablesHolder>visitStatement(statement, holder, this);
                holders.add(holder);
            }
            for (UMVariablesHolder holder : holders) {
                arg.add(holder);
            }
        }
        ((Statement) body).getVariablesHolder().setCopy(arg);
    }

    @Override
    public void visit(IfStmt n, UMVariablesHolder arg) {
        UMVariablesHolder conditionHolder = new UMVariablesHolder();
        isCondition = true;
        VoidVisitorHelper.<UMVariablesHolder>visitExpression(n.getCondition(), conditionHolder, this);
        isCondition = false;

        UMVariablesHolder thenHolder = new UMVariablesHolder();
        VoidVisitorHelper.<UMVariablesHolder>visitStatement(n.getThenStmt(), thenHolder, this);

        if (n.getElseStmt() != null) {
            UMVariablesHolder elseHolder = new UMVariablesHolder();
            VoidVisitorHelper.<UMVariablesHolder>visitStatement(n.getElseStmt(), elseHolder, this);
            arg.add(elseHolder);
        }

        arg.add(conditionHolder);
        arg.add(thenHolder);
    }

    @Override
    public void visit(WhileStmt n, UMVariablesHolder arg) {
        UMVariablesHolder conditionHolder = new UMVariablesHolder();
        isCondition = true;
        VoidVisitorHelper.<UMVariablesHolder>visitExpression(n.getCondition(), conditionHolder, this);
        isCondition = false;

        UMVariablesHolder bodyHolder = new UMVariablesHolder();
        VoidVisitorHelper.<UMVariablesHolder>visitStatement(n.getBody(), bodyHolder, this);

        arg.add(conditionHolder);
        arg.add(bodyHolder);
    }

    @Override
    public void visit(ForeachStmt n, UMVariablesHolder arg) {
        UMVariablesHolder conditionHolder = new UMVariablesHolder();
        isCondition = true;
        VoidVisitorHelper.<UMVariablesHolder>visitExpression(n.getVariable(), conditionHolder, this);
        isCondition = false;

        UMVariablesHolder bodyHolder = new UMVariablesHolder();
        VoidVisitorHelper.<UMVariablesHolder>visitStatement(n.getBody(), bodyHolder, this);

        arg.add(conditionHolder);
        arg.add(bodyHolder);
    }

    @Override
    public void visit(ForStmt n, UMVariablesHolder arg) {
        UMVariablesHolder initHolder = null;
        if (n.getInit() != null) {
            initHolder = new UMVariablesHolder();
            isCondition = true;
            //only one init expression is available by style
            VoidVisitorHelper.<UMVariablesHolder>visitExpression(n.getInit().get(0), initHolder, this);
            isCondition = false;
        }

        UMVariablesHolder conditionHolder = null;
        if (n.getCompare() != null) {
            conditionHolder = new UMVariablesHolder();
            isCondition = true;
            VoidVisitorHelper.<UMVariablesHolder>visitExpression(n.getCompare(), conditionHolder, this);
            isCondition = false;
        }

        UMVariablesHolder updateHolder = null;
        if (n.getUpdate() != null) {
            updateHolder = new UMVariablesHolder();
            isCondition = true;
            //only one update expression is available by style
            VoidVisitorHelper.<UMVariablesHolder>visitExpression(n.getUpdate().get(0), updateHolder, this);
            isCondition = false;
        }

        UMVariablesHolder bodyHolder = new UMVariablesHolder();
        VoidVisitorHelper.<UMVariablesHolder>visitStatement(n.getBody(), bodyHolder, this);

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
