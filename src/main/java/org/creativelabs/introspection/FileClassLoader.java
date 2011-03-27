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


        return loadClassData(is, size);
    }

    private byte[] readFromFile(String name) throws IOException {
        String filename = name.replace('.', File.separatorChar) + ".class";

        File f = new File (root, filename);

        FileInputStream fis = new FileInputStream(f);

        return loadClassData(fis, (int)f.length());
    }

    private byte[] loadClassData(InputStream is, int size) throws IOException {
        // Create a file object relative to directory provided
        byte buff[] = new byte[size];

        DataInputStream dis = new DataInputStream (is);

        dis.readFully(buff);

        dis.close();

        return buff;
    }
}
