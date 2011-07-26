package org.creativelabs;

import japa.parser.ast.body.*;

import org.creativelabs.introspection.*;
import org.creativelabs.ssa.holder.SimpleMultiHolderBuilder;
import org.creativelabs.typefinder.DependencyCounterVisitorBuilder;
import org.creativelabs.typefinder.ImportList;
import org.creativelabs.typefinder.VariableList;
import org.creativelabs.typefinder.VariableListBuilder;

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
        return new VariableListBuilder().setImports(imports).buildFromClass(typeDeclaration);
    }

    ClassProcessor buildClassProcessor() {
        if (imports == null) {
            throw new IllegalStateException("Imports couldn't be null.");
        }
        if (typeDeclaration == null) {
            throw new IllegalStateException("TypeDeclaration couldn't be null.");
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

        SimpleMultiHolderBuilder holderBuilder = new SimpleMultiHolderBuilder()
                .setImportList(imports)
                .setClassType(classValue);

        return new ClassProcessor(
                typeDeclaration,
                dependencyCounterBuilder,
                imports,
                holderBuilder);
    }
}
