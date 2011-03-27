package org.creativelabs.introspection;

import java.io.*;
import java.util.*;
import java.util.jar.*;

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
            byte[] data;

            try {
                if (root.endsWith(".jar")) {
                    data = readFromJarFile(name);
                } else {
                    data = readFromFile(name);
                }
            } catch (IOException e) {
                throw new ClassNotFoundException(e.getMessage());
            }

            c = defineClass (name, data, 0, data.length);

            if (c == null) {
                throw new ClassNotFoundException (name);
            }

            if (resolve) {
                resolveClass(c);
            }
        }

        return c;
    }

    private byte[] readFromJarFile(String name) throws IOException {
        String filename = name.replace('.', File.separatorChar) + ".class";

        JarFile jarFile = new JarFile(root);
        JarEntry entry = jarFile.getJarEntry(filename);
        InputStream is = jarFile.getInputStream(entry);

        int size = (int)entry.getSize();

        byte buff[] = new byte[size];

        DataInputStream dis = new DataInputStream (is);

        dis.readFully(buff);

        dis.close();

        return buff;
    }

    private byte[] readFromFile(String name) throws IOException {
        String filename = name.replace('.', File.separatorChar) + ".class";

        return loadClassData(filename);
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
