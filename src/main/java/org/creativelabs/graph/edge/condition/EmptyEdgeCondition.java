package org.creativelabs.graph.edge.condition;

/**
 * @author azotcsit
 * Date: 09.04.11
 * Time: 16:38
 */
public class EmptyEdgeCondition implements EdgeCondition{

    @Override
    public String getStringRepresentation() {
        return "";
    }

    @Override
    public EdgeCondition and(EdgeCondition condition) {
        throw new UnsupportedOperationException("Add operation is not supported by EmptyEdgeCondition class.");
    }

    @Override
    public EdgeCondition or(EdgeCondition condition) {
        throw new UnsupportedOperationException("Or operation is not supported by EmptyEdgeCondition class.");
    }

    @Override
    public EdgeCondition not() {
        throw new UnsupportedOperationException("Not operation is not supported by EmptyEdgeCondition class.");
    }
}
