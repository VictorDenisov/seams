package org.creativelabs.copy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Ast statements and expressions is not serializable in javaparser. Need to hack the javaparser's basic classes.
 */
@SuppressWarnings("unchecked")
public class CopyingUtils {

    public static <T> T copy(Object o) {
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
