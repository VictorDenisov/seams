package org.creativelabs.graph.condition;

/**
 * @author azotcsit
 *         Date: 22.04.11
 *         Time: 21:41
 */
public interface VertexConditions {
    Condition getInternalCondition();
    Condition getExternalCondition();
    static final String EMPTY_CONDITIONS_STRING = "[ | ]";
}
