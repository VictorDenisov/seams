package org.creativelabs;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;

import org.creativelabs.introspection.*;

public class ClassProcessorBuilder {

    protected ClassOrInterfaceDeclaration typeDeclaration;

    protected ImportList imports;

    protected VariableList fieldList;

    protected DependencyCounterVisitor dependencyCounter;

    public ClassProcessorBuilder setImports(ImportList importsVal) {
        this.imports = importsVal;
        return this;
    }

    public ClassProcessorBuilder setPackage(String packageVal) {
        return this;
    }

    public ClassProcessorBuilder setTypeDeclaration(ClassOrInterfaceDeclaration typeDeclarationVal) {
        this.typeDeclaration = typeDeclarationVal;
        return this;
    }

    DependencyCounterVisitor constructDependencyCounterVisitor() {
        return new DependencyCounterVisitor(fieldList, imports);
    }

    VariableList constructVariableList() {
        return new VariableList(typeDeclaration, imports);
    }

    ClassProcessor buildClassProcessor() {
        if (imports == null) {
            throw new IllegalStateException();
        }
        if (typeDeclaration == null) {
            throw new IllegalStateException();
        }
        fieldList = constructVariableList();
        fieldList.put("this", imports.getClassByShortName(typeDeclaration.getName()));

        ClassType classValue = null;

        if (typeDeclaration.getExtends() != null) {
            String classShortName = typeDeclaration.getExtends().get(0).getName();
            classValue = imports.getClassByShortName(classShortName);
        } else {
            classValue = imports.getClassByShortName("Object");
        }

        fieldList.put("super", classValue);

        dependencyCounter = constructDependencyCounterVisitor();
        return new ClassProcessor(typeDeclaration, dependencyCounter);
    }
}
