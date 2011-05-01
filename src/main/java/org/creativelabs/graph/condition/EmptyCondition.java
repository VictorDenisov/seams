package org.creativelabs.graph.condition;

/**
 * @author azotcsit
 * Date: 09.04.11
 * Time: 16:38
 */

//TODO to refactor

public class EmptyCondition implements Condition {

    @Override
    public String getStringRepresentation() {
        return "";
    }

    @Override
    public Condition and(Condition condition) {
        return condition;
//        throw new UnsupportedOperationException("Add operation is not supported by EmptyCondition class.");
    }

    @Override
    public Condition or(Condition condition) {
        return condition;
//        throw new UnsupportedOperationException("Or operation is not supported by EmptyCondition class.");
    }

    @Override
    public Condition not() {
        return this;
//        throw new UnsupportedOperationException("Not operation is not supported by EmptyCondition class.");
    }
}
