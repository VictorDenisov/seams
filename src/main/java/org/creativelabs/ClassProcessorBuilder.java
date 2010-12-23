package org.creativelabs;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;

public class ClassProcessorBuilder {

    protected ClassOrInterfaceDeclaration typeDeclaration;

    protected ImportList imports;

    protected VariableList fieldList;

    protected String packageName;

    protected DependencyCounterVisitor dependencyCounter;

    public ClassProcessorBuilder setImports(ImportList importsVal) {
        this.imports = importsVal;
        return this;
    }

    public ClassProcessorBuilder setPackage(String packageVal) {
        this.packageName = packageVal;
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
        if (packageName == null) {
            throw new IllegalStateException();
        }
        fieldList = constructVariableList();
        fieldList.put("this", packageName + "." + typeDeclaration.getName());
        if (typeDeclaration.getExtends() != null) {
            String classShortName = typeDeclaration.getExtends().get(0).getName();
            String classValue = imports.getClassByShortName(classShortName).toStringRepresentation();
            fieldList.put("super", classValue);
        } else {
            fieldList.put("super", "java.lang.Object");
        }
        dependencyCounter = constructDependencyCounterVisitor();
        return new ClassProcessor(typeDeclaration, dependencyCounter);
    }
}
