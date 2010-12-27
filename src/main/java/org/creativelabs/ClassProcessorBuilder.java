package org.creativelabs;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;

import org.creativelabs.introspection.*;

public class ClassProcessorBuilder {

    protected ClassOrInterfaceDeclaration typeDeclaration;

    protected ImportList imports;

    protected VariableList fieldList;

    protected DependencyCounterVisitorBuilder dependencyCounterBuilder;

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

    DependencyCounterVisitorBuilder constructDependencyCounterVisitor() {
        DependencyCounterVisitorBuilder builder = new DependencyCounterVisitorBuilder();
        return builder
            .setClassFields(fieldList)
            .setImports(imports);
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

        dependencyCounterBuilder = constructDependencyCounterVisitor();
        return new ClassProcessor(typeDeclaration, dependencyCounterBuilder);
    }
}
