package org.creativelabs;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;

import java.util.*;

class ImportList {

    private List<ImportDeclaration> list = new ArrayList<ImportDeclaration>();

    private Map<String, String> map;

    ImportList(CompilationUnit cu) {
        if (cu.getImports() != null) {
            list = cu.getImports();
        }
        map = new HashMap<String, String>();
        for (ImportDeclaration d : list) {
            map.put(d.getName().getName(), d.getName().toString());
        }
    }

    List<String> getImports() {
        List<String> result = new ArrayList<String>();
        for (ImportDeclaration d : list) {
            result.add(d.getName().toString());
        }
        return result;
    }

    String get(String key) {
        return map.get(key);
    }

    Class getClassByShortName(String shortName) throws ClassNotFoundException {
        for (ImportDeclaration id : list) {
            if (id.isAsterisk()) {
                try {
                    return Class.forName(id.getName().toString() + "." + shortName);
                } catch (ClassNotFoundException e) {

                }
            } else {
                if (id.getName().getName().equals(shortName)) {
                    return Class.forName(id.getName().toString());
                }
            }
        }
        throw new RuntimeException("import list can't be empty");
    }

    boolean containsKey(String key) {
        return map.containsKey(key);
    }
}
