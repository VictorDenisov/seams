package org.creativelabs.graph.condition.bool;

import org.creativelabs.graph.condition.Condition;

/**
 * @author azotcsit
 * Date: 19.04.11
 * Time: 23:19
 */
public class FalseBooleanCondition implements Condition {
    @Override
    public String getStringRepresentation() {
        return "false";
    }

    @Override
    public Condition and(Condition condition) {
        return this;
    }

    @Override
    public Condition or(Condition condition) {
        return condition;
    }

    @Override
    public Condition not() {
        return new TrueBooleanCondition();
    }
}
