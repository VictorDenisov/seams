package org.creativelabs.ssa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Ast statements and expressions is not serializable in javaparser. Need to hack the javaparser's basic classes.
 *
 * @param <T> generic type
 */
public class CopyingUtils <T> {

    public T copy(Object o) {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bout);
            out.writeObject(o);
            out.close();

            ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
            ObjectInputStream in = new ObjectInputStream(bin);
            Object ret = in.readObject();
            in.close();
            return (T) ret;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
