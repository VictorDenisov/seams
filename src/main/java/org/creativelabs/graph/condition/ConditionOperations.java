package org.creativelabs.graph.condition;

import java.io.Serializable;

/**
 * @author azotcsit
 * Date: 08.04.11
 * Time: 22:49
 */
public interface ConditionOperations extends Serializable {
    Condition and(Condition condition);
    Condition or(Condition condition);
    Condition not();
}