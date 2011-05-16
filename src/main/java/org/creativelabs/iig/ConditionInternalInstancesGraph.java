package org.creativelabs.iig;

import org.creativelabs.graph.*;
import org.creativelabs.graph.condition.Condition;
import org.creativelabs.graph.condition.EmptyCondition;

import java.util.*;

/**
 * It's internal instances graph with edge edgeConditions.
 *
 * @author azotcsit
 * Date: 09.04.11
 * Time: 0:40
 */
public class ConditionInternalInstancesGraph implements InternalInstancesGraph {
    private Set<String> allVertexes = new HashSet<String>();
    private List<String> fromVertexes = new ArrayList<String>();
    private List<String> toVertexes = new ArrayList<String>();
    private Map<String, Condition> internalVertexConditions = new HashMap<String, Condition>();
    private Map<String, Condition> externalVertexConditions = new HashMap<String, Condition>();

    @Override
    public void addEdge(String from, String to) {
        allVertexes.add(from);
        allVertexes.add(to);
        fromVertexes.add(from);
        toVertexes.add(to);
    }

    @Override
    public void addVertexConditions(String vertex, Condition internalCondition, Condition externalCondition) {
        allVertexes.add(vertex);
        internalVertexConditions.put(vertex, internalCondition);
        externalVertexConditions.put(vertex, externalCondition);
    }

    @Override
    public Condition getInternalVertexCondition(String vertex) {
        Condition condition = internalVertexConditions.get(vertex);
        if (condition == null) {
            return new EmptyCondition();
        }
        return condition;
    }

    @Override
    public Condition getExternalVertexCondition(String vertex) {
        Condition condition = externalVertexConditions.get(vertex);
        if (condition == null) {
            return new EmptyCondition();
        }
        return condition;
    }

    public boolean contains(String variable) {
        return fromVertexes.contains(variable);
    }

    public Set<String> toSet() {
        return new HashSet<String>(fromVertexes);
    }

    public void buildGraph(GraphBuilder graphBuilder) {
        Map<String, Vertex> map = new HashMap<String, Vertex>();

        for (String vertex : allVertexes) {
            if (internalVertexConditions.containsKey(vertex)
                    && externalVertexConditions.containsKey(vertex)) {
                map.put(vertex, graphBuilder.addVertex(vertex,
                        internalVertexConditions.get(vertex).copy(),
                        externalVertexConditions.get(vertex).copy()));
            } else {
                map.put(vertex, graphBuilder.addVertex(vertex,
                        new EmptyCondition(),
                        new EmptyCondition()));
            }
        }

        for (int i = 0; i < fromVertexes.size(); i++) {
            Vertex a = map.get(fromVertexes.get(i));
            Vertex b = map.get(toVertexes.get(i));
            graphBuilder.addEdge(a, b);
        }
    }
}
