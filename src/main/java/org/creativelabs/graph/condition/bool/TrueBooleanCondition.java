package org.creativelabs.graph.condition.bool;

import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.EmptyCondition;

/**
 * @author azotcsit
 *         Date: 19.04.11
 *         Time: 23:19
 */
public class TrueBooleanCondition implements Condition {
    @Override
    public String getStringRepresentation() {
        return "true";
    }

    @Override
    public Condition and(Condition condition) {
        if (condition instanceof EmptyCondition) {
            return new TrueBooleanCondition();
        }
        return condition.copy();
    }

    @Override
    public Condition or(Condition condition) {
        return new TrueBooleanCondition();
    }

    @Override
    public Condition not() {
        return new FalseBooleanCondition();
    }

    @Override
    public Condition copy() {
        return new TrueBooleanCondition();
    }
}
