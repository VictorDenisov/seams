package org.creativelabs;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.type.*;

import org.creativelabs.introspection.*;

public class ClassProcessorBuilder {

    protected ClassOrInterfaceDeclaration typeDeclaration;

    protected ImportList imports;

    protected VariableList fieldList;

    protected DependencyCounterVisitorBuilder dependencyCounterBuilder;

    protected VariableListBuilder variableListBuilder;

    public ClassProcessorBuilder setImports(ImportList importsVal) {
        this.imports = importsVal;
        return this;
    }

    public ClassProcessorBuilder setVariableListBuilder(VariableListBuilder variableListBuilder) {
        this.variableListBuilder = variableListBuilder;
        return this;
    }

    public ClassProcessorBuilder setPackage(String packageVal) {
        return this;
    }

    public ClassProcessorBuilder setDependencyCounterBuilder(
            DependencyCounterVisitorBuilder dependencyCounterBuilder) {

        this.dependencyCounterBuilder = dependencyCounterBuilder;
        return this;
    }

    public ClassProcessorBuilder setTypeDeclaration(ClassOrInterfaceDeclaration typeDeclarationVal) {
        this.typeDeclaration = typeDeclarationVal;
        return this;
    }

    DependencyCounterVisitorBuilder constructDependencyCounterVisitor() {
        return dependencyCounterBuilder
            .setClassFields(fieldList)
            .setImports(imports);
    }

    VariableList constructVariableList() {
        return variableListBuilder.setImports(imports).buildFromClass(typeDeclaration);
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
            ClassOrInterfaceType classShortName = typeDeclaration.getExtends().get(0);
            classValue = imports.getClassByClassOrInterfaceType(classShortName);
        } else {
            classValue = imports.getClassByShortName("Object");
        }

        fieldList.put("super", classValue);

        dependencyCounterBuilder = constructDependencyCounterVisitor();
        return new ClassProcessor(typeDeclaration, dependencyCounterBuilder);
    }
}
