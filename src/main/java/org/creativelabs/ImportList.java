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

    boolean containsKey(String key) {
        return map.containsKey(key);
    }
}
