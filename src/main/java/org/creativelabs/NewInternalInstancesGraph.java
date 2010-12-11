package org.creativelabs;

import java.util.*;

class NewInternalInstancesGraph {

    HashMap<String, String> edges = new HashMap<String, String>();

    void add(String source, String target) {
        edges.put(source, target);
    }

    public String toString() {
        StringBuffer result = new StringBuffer("{");
        for (Map.Entry<String, String> entry : edges.entrySet()) {
            result.append(entry.getKey() + " -> " + entry.getValue() + ",");
        }
        return result.toString() + "}";
    }

    public boolean contains(String variable) {
        return edges.containsKey(variable);
    }

    public Set<String> toSet() {
        return edges.keySet();
    }
}
