package org.creativelabs;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.expr.NameExpr;

import java.util.*;

class ImportList {

    private List<ImportDeclaration> list = new ArrayList<ImportDeclaration>();

    private Map<String, String> map;

    ImportList(CompilationUnit cu) {
        list.add(new ImportDeclaration(new NameExpr("java.lang"), false, true));
        if (cu.getImports() != null) {
            list.addAll(cu.getImports());
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

    private Class getClassForName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    Class getClassByShortName(String shortName) throws ClassNotFoundException {
        for (ImportDeclaration id : list) {
            if (id.isAsterisk()) {
                Class clazz =  getClassForName(id.getName().toString() + "." + shortName);
                if (clazz != null) {
                    return clazz;
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
