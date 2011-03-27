package org.creativelabs.introspection;

import java.io.*;
import java.util.*;
import java.util.jar.*;

public class FileClassLoader extends ClassLoader {

    private String[] files;

    public FileClassLoader(String[] files) {
        if (files == null) {
            throw new IllegalArgumentException("Empty files list");
        }
        if (files.length == 0) {
            throw new IllegalArgumentException("Empty files list");
        }
        this.files = files;
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
            filesloop:
            for (String root : files) {
                byte[] data;

                try {
                    if (root.endsWith(".jar")) {
                        data = readFromJarFile(root, name);
                    } else {
                        data = readFromFile(root, name);
                    }
                } catch (IOException e) {
                    continue filesloop;
                }

                c = defineClass (name, data, 0, data.length);

                if (c == null) {
                    continue filesloop;
                }

                if (resolve) {
                    resolveClass(c);
                }
                break;
            }
        }

        return c;
    }

    private byte[] readFromJarFile(String root, String name) throws IOException {
        String filename = name.replace('.', File.separatorChar) + ".class";

        JarFile jarFile = new JarFile(root);
        JarEntry entry = jarFile.getJarEntry(filename);
        if (entry == null) {
            throw new FileNotFoundException(name);
        }
        InputStream is = jarFile.getInputStream(entry);

        int size = (int)entry.getSize();


        return loadClassData(is, size);
    }

    private byte[] readFromFile(String root, String name) throws IOException {
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
