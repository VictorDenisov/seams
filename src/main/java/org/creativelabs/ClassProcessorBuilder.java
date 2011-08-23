package org.creativelabs;

import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.type.ClassOrInterfaceType;
import org.creativelabs.introspection.ClassType;
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
            throw new IllegalStateException("Imports couldn't be null.");
        }
        if (typeDeclaration == null) {
            throw new IllegalStateException("TypeDeclaration couldn't be null.");
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

        SimpleMultiHolderBuilder holderBuilder = new SimpleMultiHolderBuilder()
                .setImportList(imports)
                .setClassType(imports.getClassByShortName(typeDeclaration.getName()));

        return new ClassProcessor(
                typeDeclaration,
                dependencyCounterBuilder,
                imports,
                holderBuilder);
    }
}
