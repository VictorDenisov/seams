package org.creativelabs.ssa.holder;

import org.creativelabs.copy.Copyable;
import org.creativelabs.graph.condition.Condition;

/**
 * @author azotcsit
 *         Date: 20.05.11
 *         Time: 21:37
 */
public interface BasicBlockConditionHolder extends Copyable {
    Condition getBasicBlockCondition();
    void setBasicBlockCondition(Condition condition);
}
