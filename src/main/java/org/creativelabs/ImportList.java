package org.creativelabs;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.expr.NameExpr;

import org.creativelabs.introspection.ReflectionAbstractionImpl;
import org.creativelabs.introspection.ClassType;

import java.util.*;

class ImportList {

    private List<ImportDeclaration> list = new ArrayList<ImportDeclaration>();

    ImportList(CompilationUnit cu) {
        list.add(new ImportDeclaration(new NameExpr("java.lang"), false, true));
        if (cu.getImports() != null) {
            list.addAll(cu.getImports());
        }
    }

    List<String> getImports() {
        List<String> result = new ArrayList<String>();
        for (ImportDeclaration d : list) {
            result.add(d.getName().toString());
        }
        return result;
    }

    ClassType getClassByShortName(String shortName) {
        ReflectionAbstractionImpl ra = new ReflectionAbstractionImpl();
        for (ImportDeclaration id : list) {
            if (id.isAsterisk()) {
                String className = id.getName().toString() + "." + shortName;
                boolean isClassExists = ra.classWithNameExists(className);
                if (isClassExists) {
                    return ra.getClassTypeByName(className);
                }
            } else {
                String className = id.getName().toString();
                if (id.getName().getName().equals(shortName)) {
                    return ra.getClassTypeByName(className);
                }
            }
        }
        throw new RuntimeException("import list can't be empty");
    }
}
