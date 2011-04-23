package org.creativelabs.ssa;


import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.ExpressionStmt;

import java.util.*;


/**
 * Class needed for store information about phi function and phi node.
 *
 * @author azotov
 */
public class PhiNode {
    /**
     * Variable's name.
     */
    private String name;
    /**
     * Indexes of left part of assignment of phi node.
     */
    private Integer leftIndex;
    /**
     * Indexes of using variables as arguments of phi function.
     */
    private Set<Integer> indexes = new TreeSet<Integer>();
    /**
     * Mode of inserting of phi function.
     */
    private Mode mode;

    /**
     * It's a link on AST representation of this phi node.
     * It's needed for changing phi node after inserting to AST.
     */
    private ExpressionStmt expressionStmt;

    public PhiNode() {
    }

    public PhiNode(String name, Integer leftIndex, Integer... indexes) {
        this.name = name;
        this.leftIndex = leftIndex;
        this.mode = PhiNode.Mode.NONE;
        this.indexes.addAll(Arrays.asList(indexes));
    }

    public PhiNode(String name, Integer leftIndex, Mode mode, Integer... indexes) {
        this.name = name;
        this.leftIndex = leftIndex;
        this.mode = mode;
        this.indexes.addAll(Arrays.asList(indexes));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLeftIndex() {
        return leftIndex;
    }

    public void setLeftIndex(Integer leftIndex) {
        this.leftIndex = leftIndex;
    }

    public void addIndex(Integer i) {
        this.indexes.add(i);
    }

    public void addAllIndex(Integer... i) {
        this.indexes.addAll(Arrays.asList(i));
    }

    public void addAllIndex(Set<Integer> i) {
        this.indexes.addAll(i);
    }

    public void removeIndex(Integer i) {
        this.indexes.remove(i);
    }

    public void removeAllIndex(Set<Integer> i) {
        this.indexes.removeAll(i);
    }

    public void removeAllIndex(Integer... i) {
        this.indexes.removeAll(Arrays.asList(i));
    }

    public void clearIndexes() {
        this.indexes.clear();
    }

    public Integer[] getIndexes() {
        return indexes.toArray(new Integer[indexes.size()]);
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * Mode of inserting of phi function.
     */
    public enum Mode {
        /**
         * Phi function wouldn't be inserted.
         */
        NONE,
        /**
         * Phi function would be inserted before current statement.
         */
        BEFORE,
        /**
         * Phi function would be inserted after current statement.
         */
        AFTER,
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhiNode phiNode = (PhiNode) o;

        if (indexes != null ? !indexes.equals(phiNode.indexes) : phiNode.indexes != null) return false;
        if (leftIndex != null ? !leftIndex.equals(phiNode.leftIndex) : phiNode.leftIndex != null) return false;
        if (mode != phiNode.mode) return false;
        if (name != null ? !name.equals(phiNode.name) : phiNode.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (leftIndex != null ? leftIndex.hashCode() : 0);
        result = 31 * result + (indexes != null ? indexes.hashCode() : 0);
        result = 31 * result + (mode != null ? mode.hashCode() : 0);
        return result;
    }

    public ExpressionStmt convertToExprStmt() {
        List<Expression> arguments = new ArrayList<Expression>();
        for (Integer index : indexes) {
            arguments.add(new NameExpr(name + SsaFormConverter.SEPARATOR + index));
        }
        expressionStmt = new ExpressionStmt(
                new AssignExpr(new NameExpr(name + SsaFormConverter.SEPARATOR + leftIndex),
                        new MethodCallExpr(null, "#phi", arguments),
                        AssignExpr.Operator.assign));
        return expressionStmt;
    }

    public void addIndexToExprStmt(Integer index) {
        if (expressionStmt != null) {
            ((MethodCallExpr) ((AssignExpr) expressionStmt.getExpression()).getValue()).getArgs().add(new NameExpr(name + SsaFormConverter.SEPARATOR + index));
        }
    }
}
