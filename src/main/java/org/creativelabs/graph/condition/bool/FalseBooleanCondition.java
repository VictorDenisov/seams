package org.creativelabs.graph.condition.bool;

import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.EmptyCondition;

/**
 * @author azotcsit
 *         Date: 19.04.11
 *         Time: 23:19
 */
public class FalseBooleanCondition implements Condition {
    @Override
    public String getStringRepresentation() {
        return "false";
    }

    @Override
    public Condition and(Condition condition) {
        return new FalseBooleanCondition();
    }

    @Override
    public Condition or(Condition condition) {
        if (condition instanceof EmptyCondition) {
            return new FalseBooleanCondition();
        }
        return condition.copy();
    }

    @Override
    public Condition not() {
        return new TrueBooleanCondition();
    }

    @Override
    public Condition copy() {
        return new FalseBooleanCondition();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmptyCondition that = (EmptyCondition) o;

        if (getStringRepresentation() != null ?
                !getStringRepresentation().equals(that.getStringRepresentation()) :
                that.getStringRepresentation() != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return getStringRepresentation().hashCode();
    }
}
