package org.creativelabs.ssa.holder;

import org.creativelabs.copy.CopyingUtils;
import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.EmptyCondition;

/**
 * @author azotcsit
 *         Date: 20.05.11
 *         Time: 22:06
 */
public class SimpleConditionHolder implements BasicBlockConditionHolder {

    private Condition condition;

    public SimpleConditionHolder() {
        this.condition = new EmptyCondition();
    }

    public SimpleConditionHolder(Condition condition) {
        this.condition = condition;
    }

    @Override
    public Condition getBasicBlockCondition() {
        return condition;
    }

    @Override
    public void setBasicBlockCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public SimpleConditionHolder copy() {
        return CopyingUtils.<SimpleConditionHolder>copy(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleConditionHolder holder = (SimpleConditionHolder) o;

        if (condition != null ? !condition.equals(holder.condition) : holder.condition != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return condition != null ? condition.hashCode() : 0;
    }
}
