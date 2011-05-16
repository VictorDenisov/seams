package org.creativelabs.graph.condition;

import org.creativelabs.Copying;

/**
 * @author azotcsit
 * Date: 08.04.11
 * Time: 21:58
 */
public interface Condition extends ConditionOperations, Copying<Condition> {
    String getStringRepresentation();
}
