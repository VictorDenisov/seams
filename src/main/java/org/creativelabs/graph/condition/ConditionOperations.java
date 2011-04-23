package org.creativelabs.graph.condition;

/**
 * @author azotcsit
 * Date: 08.04.11
 * Time: 22:49
 */
public interface ConditionOperations {
    Condition and(Condition condition);
    Condition or(Condition condition);
    Condition not();
}
