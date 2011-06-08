package org.creativelabs.helper;

import japa.parser.ast.*;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.visitor.VoidVisitorAdapter;

/**
 * This class helps for visiting the statements and expressions.
 *
 * @author azotcsit
 *         Date: 30.04.11
 *         Time: 14:23
 */
public class VoidVisitorHelper {

    /**
     * Visits a statement by visitor with some arguments.
     *
     * @param statement for processing
     * @param arg       arguments are used in processing
     * @param visitor   the using visitor
     * @param <T>       the type of argument
     */
    public static <T> void visitStatement(Statement statement, T arg, VoidVisitorAdapter<T> visitor) {
        if (statement instanceof AssertStmt) {
            visitor.visit((AssertStmt) statement, arg);
        } else if (statement instanceof BlockStmt) {
            visitor.visit((BlockStmt) statement, arg);
        } else if (statement instanceof BreakStmt) {
            visitor.visit((BreakStmt) statement, arg);
        } else if (statement instanceof ContinueStmt) {
            visitor.visit((ContinueStmt) statement, arg);
        } else if (statement instanceof DoStmt) {
            visitor.visit((DoStmt) statement, arg);
        } else if (statement instanceof EmptyStmt) {
            visitor.visit((EmptyStmt) statement, arg);
        } else if (statement instanceof ExplicitConstructorInvocationStmt) {
            visitor.visit((ExplicitConstructorInvocationStmt) statement, arg);
        } else if (statement instanceof ExpressionStmt) {
            visitor.visit((ExpressionStmt) statement, arg);
        } else if (statement instanceof ForeachStmt) {
            visitor.visit((ForeachStmt) statement, arg);
        } else if (statement instanceof ForStmt) {
            visitor.visit((ForStmt) statement, arg);
        } else if (statement instanceof IfStmt) {
            visitor.visit((IfStmt) statement, arg);
        } else if (statement instanceof LabeledStmt) {
            visitor.visit((LabeledStmt) statement, arg);
        } else if (statement instanceof ReturnStmt) {
            visitor.visit((ReturnStmt) statement, arg);
        } else if (statement instanceof SwitchEntryStmt) {
            visitor.visit((SwitchEntryStmt) statement, arg);
        } else if (statement instanceof SwitchStmt) {
            visitor.visit((SwitchStmt) statement, arg);
        } else if (statement instanceof SynchronizedStmt) {
            visitor.visit((SynchronizedStmt) statement, arg);
        } else if (statement instanceof ThrowStmt) {
            visitor.visit((ThrowStmt) statement, arg);
        } else if (statement instanceof TryStmt) {
            visitor.visit((TryStmt) statement, arg);
        } else if (statement instanceof TypeDeclarationStmt) {
            visitor.visit((TypeDeclarationStmt) statement, arg);
        } else if (statement instanceof WhileStmt) {
            visitor.visit((WhileStmt) statement, arg);
        } else {
            throw new UnsupportedOperationException("VoidVisitorHelper is not support statement for " +
                    statement.getClass() + ".");
        }
    }

    /**
     * Visits a expression by visitor with some arguments.
     *
     * @param expression for processing
     * @param arg        arguments are used in processing
     * @param visitor    the using visitor
     * @param <T>        the type of argument
     */
    public static <T> void visitExpression(Expression expression, T arg, VoidVisitorAdapter<T> visitor) {
        if (expression instanceof ArrayAccessExpr) {
            visitor.visit((ArrayAccessExpr) expression, arg);
        } else if (expression instanceof ArrayCreationExpr) {
            visitor.visit((ArrayCreationExpr) expression, arg);
        } else if (expression instanceof ArrayInitializerExpr) {
            visitor.visit((ArrayInitializerExpr) expression, arg);
        } else if (expression instanceof AssignExpr) {
            visitor.visit((AssignExpr) expression, arg);
        } else if (expression instanceof BinaryExpr) {
            visitor.visit((BinaryExpr) expression, arg);
        } else if (expression instanceof BooleanLiteralExpr) {
            visitor.visit((BooleanLiteralExpr) expression, arg);
        } else if (expression instanceof CastExpr) {
            visitor.visit((CastExpr) expression, arg);
        } else if (expression instanceof CharLiteralExpr) {
            visitor.visit((CharLiteralExpr) expression, arg);
        } else if (expression instanceof ClassExpr) {
            visitor.visit((ClassExpr) expression, arg);
        } else if (expression instanceof ConditionalExpr) {
            visitor.visit((ConditionalExpr) expression, arg);
        } else if (expression instanceof DoubleLiteralExpr) {
            visitor.visit((DoubleLiteralExpr) expression, arg);
        } else if (expression instanceof EnclosedExpr) {
            visitor.visit((EnclosedExpr) expression, arg);
        } else if (expression instanceof FieldAccessExpr) {
            visitor.visit((FieldAccessExpr) expression, arg);
        } else if (expression instanceof InstanceOfExpr) {
            visitor.visit((InstanceOfExpr) expression, arg);
        } else if (expression instanceof IntegerLiteralMinValueExpr) {
            visitor.visit((IntegerLiteralMinValueExpr) expression, arg);
        } else if (expression instanceof IntegerLiteralExpr) {
            visitor.visit((IntegerLiteralExpr) expression, arg);
        } else if (expression instanceof LongLiteralMinValueExpr) {
            visitor.visit((LongLiteralMinValueExpr) expression, arg);
        } else if (expression instanceof LongLiteralExpr) {
            visitor.visit((LongLiteralExpr) expression, arg);
        } else if (expression instanceof MarkerAnnotationExpr) {
            visitor.visit((MarkerAnnotationExpr) expression, arg);
        } else if (expression instanceof MethodCallExpr) {
            visitor.visit((MethodCallExpr) expression, arg);
        } else if (expression instanceof QualifiedNameExpr) {
            visitor.visit((QualifiedNameExpr) expression, arg);
        } else if (expression instanceof NameExpr) {
            visitor.visit((NameExpr) expression, arg);
        } else if (expression instanceof NormalAnnotationExpr) {
            visitor.visit((NormalAnnotationExpr) expression, arg);
        } else if (expression instanceof NullLiteralExpr) {
            visitor.visit((NullLiteralExpr) expression, arg);
        } else if (expression instanceof ObjectCreationExpr) {
            visitor.visit((ObjectCreationExpr) expression, arg);
        } else if (expression instanceof SingleMemberAnnotationExpr) {
            visitor.visit((SingleMemberAnnotationExpr) expression, arg);
        } else if (expression instanceof StringLiteralExpr) {
            visitor.visit((StringLiteralExpr) expression, arg);
        } else if (expression instanceof SuperExpr) {
            visitor.visit((SuperExpr) expression, arg);
        } else if (expression instanceof ThisExpr) {
            visitor.visit((ThisExpr) expression, arg);
        } else if (expression instanceof UnaryExpr) {
            visitor.visit((UnaryExpr) expression, arg);
        } else if (expression instanceof VariableDeclarationExpr) {
            visitor.visit((VariableDeclarationExpr) expression, arg);
        } else {
            throw new UnsupportedOperationException("VoidVisitorHelper is not support expression for " +
                    expression.getClass() + ".");
        }
    }

    /**
     * Visits a comment by visitor with some arguments.
     *
     * @param comment for processing
     * @param arg     arguments are used in processing
     * @param visitor the using visitor
     * @param <T>     the type of argument
     */
    public static <T> void visitComment(Comment comment, T arg, VoidVisitorAdapter<T> visitor) {
        if (comment instanceof LineComment) {
            visitor.visit((LineComment) comment, arg);
        } else if (comment instanceof BlockComment) {
            visitor.visit((BlockComment) comment, arg);
        } else if (comment instanceof JavadocComment) {
            visitor.visit((JavadocComment) comment, arg);
        } else {
            throw new UnsupportedOperationException("VoidVisitorHelper is not support comment for " +
                    comment.getClass() + ".");
        }
    }

    /**
     * Visits a node by visitor with some arguments.
     *
     * @param node    for processing
     * @param arg     arguments are used in processing
     * @param visitor the using visitor
     * @param <T>     the type of argument
     */
    public static <T> void visitNode(Node node, T arg, VoidVisitorAdapter<T> visitor) {
        if (node instanceof CatchClause) {
            visitor.visit((CatchClause) node, arg);
        } else if (node instanceof MemberValuePair) {
            visitor.visit((MemberValuePair) node, arg);
        } else if (node instanceof CompilationUnit) {
            visitor.visit((CompilationUnit) node, arg);
        } else if (node instanceof PackageDeclaration) {
            visitor.visit((PackageDeclaration) node, arg);
        } else if (node instanceof TypeParameter) {
            visitor.visit((TypeParameter) node, arg);
        } else if (node instanceof Statement) {
            visitStatement((Statement) node, arg, visitor);
        } else if (node instanceof Expression) {
            visitExpression((Expression) node, arg, visitor);
        } else if (node instanceof Comment) {
            visitComment((Comment) node, arg, visitor);
        } else {
            //TODO: to addEdge processing other constructions
            throw new UnsupportedOperationException("VoidVisitorHelper is not support node for " +
                    node.getClass() + ".");
        }
    }

}
