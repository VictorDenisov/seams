package org.creativelabs;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.expr.NameExpr;

import org.creativelabs.introspection.ReflectionAbstractionImpl;

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

    String getClassByShortName(String shortName) {
        for (ImportDeclaration id : list) {
            if (id.isAsterisk()) {
                String className = id.getName().toString() + "." + shortName;
                boolean isClassExists = new ReflectionAbstractionImpl().classWithNameExists(className);
                if (isClassExists) {
                    return className;
                }
            } else {
                String className = id.getName().toString();
                if (id.getName().getName().equals(shortName)) {
                    return className;
                }
            }
        }
        throw new RuntimeException("import list can't be empty");
    }
}
