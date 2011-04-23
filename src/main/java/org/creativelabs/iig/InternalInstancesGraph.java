package org.creativelabs.iig;

import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.GraphBuilder;

import java.util.Set;

/**
 * @author azotcsit
 * Date: 09.04.11
 * Time: 16:37
 */
public interface InternalInstancesGraph {
    void add(String source, String target);
    void add(String source, String target, Condition condition);
    boolean contains(String variable);
    Set<String> toSet();
    void buildGraph(GraphBuilder graphBuilder);
}
