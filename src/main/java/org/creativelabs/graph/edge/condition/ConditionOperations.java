package org.creativelabs.graph.edge.condition;

/**
 * @author azotcsit
 * Date: 08.04.11
 * Time: 22:49
 */
public interface ConditionOperations {
    EdgeCondition and(EdgeCondition condition);
    EdgeCondition or(EdgeCondition condition);
    EdgeCondition not();
}
