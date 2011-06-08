package org.creativelabs.graph.condition;

import org.creativelabs.copy.Copyable;

/**
 * @author azotcsit
 * Date: 08.04.11
 * Time: 21:58
 */
public interface Condition extends ConditionOperations, Copyable {
    String getStringRepresentation();
}
