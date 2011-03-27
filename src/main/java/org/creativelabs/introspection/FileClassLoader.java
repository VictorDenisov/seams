package org.creativelabs.introspection;

import java.io.*;
import java.util.*;

public class FileClassLoader extends ClassLoader {

    private String root;

    public FileClassLoader(String rootDir) {
        if (rootDir == null) {
            throw new IllegalArgumentException("Null root directory");
        }
        this.root = rootDir;
    }

    protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class c = findLoadedClass (name);
        if (c == null) {
            try {
                c = findSystemClass(name);
            } catch (Exception e) {
                // Ignore these
            }
        }
                                
        if (c == null) {
            String filename = name.replace('.', File.separatorChar) + ".class";

            try {
                byte[] data = loadClassData(filename);

                c = defineClass (name, data, 0, data.length);

                if (c == null) {
                    throw new ClassNotFoundException (name);
                }
            } catch (IOException e) {
                throw new ClassNotFoundException ("Error reading file: " + filename);
            }

            if (resolve) {
                resolveClass(c);
            }
        }

        return c;
    }

    private byte[] loadClassData(String filename) throws IOException {
        // Create a file object relative to directory provided
        File f = new File (root, filename);

        int size = (int)f.length();

        byte buff[] = new byte[size];

        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream (fis);

        dis.readFully(buff);

        dis.close();

        return buff;
    }
}
