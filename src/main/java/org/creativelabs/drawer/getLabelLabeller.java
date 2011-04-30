package org.creativelabs.drawer;

import org.apache.commons.collections15.Transformer;

/**
 * @author azotcsit
 *         Date: 23.04.11
 *         Time: 22:08
 */
public class getLabelLabeller<V> implements Transformer<V,String> {
    @Override
    public String transform(V v) {
        return ((org.creativelabs.graph.Vertex) v).getLabel();
    }
}
