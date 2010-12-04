package org.creativelabs;

import japa.parser.ast.body.*;
import java.util.*;


class FieldList {

    List<String> fieldNames = new ArrayList<String>();

    FieldList(ClassOrInterfaceDeclaration classDeclaration, ImportList imports) {
        for (BodyDeclaration bd : classDeclaration.getMembers()) {
            if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = (FieldDeclaration) bd;
                for (VariableDeclarator vardecl : fd.getVariables()) {
                    fieldNames.add(vardecl.getId().getName());
                }
            }
        }
    }

    List<String> getNames() {
        return Collections.unmodifiableList(fieldNames);

    }
}
