package org.creativelabs;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;

public class ClassProcessorBuilder {

    private ClassOrInterfaceDeclaration typeDeclaration;

    private ImportList imports;

    public ClassProcessorBuilder setImportList(ImportList imports) {
        this.imports = imports;
        return this;
    }

    public ClassProcessorBuilder setTypeDeclaration(ClassOrInterfaceDeclaration typeDeclaration) {
        this.typeDeclaration = typeDeclaration;
        return this;
    }

    DependencyCounterVisitor constructDependencyCounterVisitor(VariableList fieldList,
            ImportList imports) {
        return new DependencyCounterVisitor(fieldList, imports);
    }

    VariableList constructVariableList(ClassOrInterfaceDeclaration typeDeclaration,
            ImportList imports) {
        return new VariableList(typeDeclaration, imports);
    }

    ClassProcessor buildClassProcessor() {
        if (imports == null) {
            throw new IllegalStateException();
        }
        if (typeDeclaration == null) {
            throw new IllegalStateException();
        }
        VariableList fieldList 
            = constructVariableList(typeDeclaration, imports);
        fieldList.put("this", typeDeclaration.getName());
        fieldList.put("super", typeDeclaration.getExtends().get(0).getName());
        DependencyCounterVisitor dependencyCounter 
            = constructDependencyCounterVisitor(fieldList, imports);
        return new ClassProcessor(typeDeclaration, dependencyCounter);
    }
}
