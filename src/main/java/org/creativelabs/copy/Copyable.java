package org.creativelabs.copy;

import java.io.Serializable;

/**
 * @author azotcsit
 *         Date: 11.05.11
 *         Time: 22:58
 */
public interface Copyable extends Serializable {
    <T> T copy();
}
