package org.creativelabs;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;

import java.util.List;
import java.util.ArrayList;

class ImportList {

    private List<ImportDeclaration> list;

    ImportList(CompilationUnit cu) {
         this.list = cu.getImports();
    }

    List<String> getImports() {
        List<String> result = new ArrayList<String>();
        for (ImportDeclaration d : list) {
            result.add(d.getName().toString());
        }
        return result;
    }

}
