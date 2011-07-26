package org.creativelabs;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.creativelabs.graph.condition.EmptyCondition;
import org.creativelabs.iig.ConditionInternalInstancesGraph;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.introspection.ClassType;
import org.creativelabs.report.ReportBuilder;
import org.creativelabs.ssa.SsaError;
import org.creativelabs.ssa.holder.SimpleMultiHolder;
import org.creativelabs.ssa.holder.SimpleMultiHolderBuilder;
import org.creativelabs.ssa.holder.variable.StringVariable;
import org.creativelabs.ssa.holder.variable.Variable;
import org.creativelabs.ssa.representation.AstSsaFormRepresentation;
import org.creativelabs.ssa.representation.SsaFormRepresentation;
import org.creativelabs.ssa.visitor.SsaFormConverter;
import org.creativelabs.typefinder.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class ClassProcessor {

    private ClassOrInterfaceDeclaration typeDeclaration;

    private Log log = LogFactory.getLog(ClassProcessor.class);

    public static String debugInfo;

    protected Map<String, Collection<Dependency>> dependencies = new HashMap<String, Collection<Dependency>>();

    private SimpleMultiHolderBuilder holderBuilder;

    private DependencyCounterVisitorBuilder dependencyCounterBuilder;

    protected Map<String, InternalInstancesGraph> internalInstances
            = new HashMap<String, InternalInstancesGraph>();
    protected Set<SsaFormRepresentation> forms = new HashSet<SsaFormRepresentation>();

    protected Set<SsaError> errors = new HashSet<SsaError>();

    protected InternalInstancesGraph ssaInternalInstancesGraph = new ConditionInternalInstancesGraph();

    protected ImportList imports;

    ClassProcessor(ClassOrInterfaceDeclaration typeDeclaration,
                   DependencyCounterVisitorBuilder dependencyCounterBuilder) {
        this.typeDeclaration = typeDeclaration;
        this.dependencyCounterBuilder = dependencyCounterBuilder;
    }

    ClassProcessor(ClassOrInterfaceDeclaration typeDeclaration, DependencyCounterVisitorBuilder dependencyCounterBuilder, SimpleMultiHolderBuilder holderBuilder) {
        this.typeDeclaration = typeDeclaration;
        this.dependencyCounterBuilder = dependencyCounterBuilder;
        this.holderBuilder = holderBuilder;
    }

    public ClassProcessor(ClassOrInterfaceDeclaration typeDeclaration, DependencyCounterVisitorBuilder dependencyCounterBuilder, ImportList imports, SimpleMultiHolderBuilder holderBuilder) {
        this.typeDeclaration = typeDeclaration;
        this.dependencyCounterBuilder = dependencyCounterBuilder;
        this.imports = imports;
        this.holderBuilder = holderBuilder;
    }

    public void buildReport(ReportBuilder reportBuilder) {
        reportBuilder.setDependencies(typeDeclaration.getName(), dependencies);
        reportBuilder.setInternalInstances(typeDeclaration.getName(), internalInstances);
        reportBuilder.setSsaFormRepresentations(typeDeclaration.getName(), forms);
        reportBuilder.setSsaErrors(typeDeclaration.getName(), errors);
    }

    public DependenciesChart getDependenciesChart() {
        DependenciesChart chart = new DependenciesChart();
        for (Map.Entry<String, InternalInstancesGraph> entry : internalInstances.entrySet()) {
            chart.addInternalInstancesCountForMethod(entry.getKey(), entry.getValue().toSet().size());
        }
        Collection<String> classDependencies = new HashSet<String>();
        for (Collection<Dependency> dependencySet : dependencies.values()) {
            for (Dependency dependency : dependencySet) {
                classDependencies.add(dependency.getType() + "");
            }
        }
        chart.addDependenciesCountForClass(typeDeclaration.getName(), classDependencies.size());
        return chart;
    }

    public Map<String, InternalInstancesGraph> getInternalInstances() {
        return internalInstances;
    }

    public InternalInstancesGraph getSsaInternalInstancesGraph() {
        return ssaInternalInstancesGraph;
    }

    public Map<String, Collection<Dependency>> getDependencies() {
        return dependencies;
    }

    public Set<SsaFormRepresentation> getSsaFormRepresentations() {
        return forms;
    }

    public Set<SsaError> getErrors() {
        return errors;
    }

    public void compute() {
        for (BodyDeclaration bd : typeDeclaration.getMembers()) {
            if (bd instanceof MethodDeclaration) {
                MethodDeclaration md = (MethodDeclaration) bd;
                findOutgoingDependencies(md);
                buildInternalInstancesGraph(md);
            }
        }
    }

    void findOutgoingDependencies(MethodDeclaration md) {
        BlockStmt body = md.getBody();
        dependencyCounterBuilder.setConstructedMethodArguments(md);
        DependencyCounterVisitor dependencyCounterVisitor = dependencyCounterBuilder.build();
        dependencyCounterVisitor.visit(body, null);

        if (dependencyCounterVisitor.getDependencies() != null) {
            dependencies.put(md.getName(), dependencyCounterVisitor.getDependencies());
        }
        if (dependencyCounterVisitor.getInternalInstances() != null) {
            internalInstances.put(md.getName(), dependencyCounterVisitor.getInternalInstances());
        }
    }

    void buildInternalInstancesGraph(MethodDeclaration md) {
        debugInfo = "Class name = " + typeDeclaration.getName() +
                "; method name = " + md.getName() + "; ";

        Map<Variable, Integer> variables = new HashMap<Variable, Integer>();
        Set<String> fields = new HashSet<String>();

        addFieldsOfCurrentClass(variables, fields, typeDeclaration);
        addFieldsOfParentClass(variables, fields, typeDeclaration);

        SsaFormConverter visitor = new SsaFormConverter(ssaInternalInstancesGraph, typeDeclaration.getName());

        holderBuilder.setCondition(new EmptyCondition())
                .setVariables(variables)
                .setFieldsNames(fields);

        SimpleMultiHolder holder = holderBuilder.buildMultiHolder();
        try {
            visitor.visit(md, holder);
            if (md == null) {
                //TODO find why md can be null
                md = new MethodDeclaration();
            }
        } catch (RuntimeException e) {
            errors.add(new SsaError(md, e, typeDeclaration.getName()));
        }

        forms.add(new AstSsaFormRepresentation(md));
        ssaInternalInstancesGraph = visitor.getGraph();
    }

    private void addField(Map<Variable, Integer> variables, Set<String> fields, String variableName) {
        variables.put(new StringVariable(variableName, Constants.THIS_SCOPE), 0);
        fields.add(variableName);

//                    ssaInternalInstancesGraph.addEdge(vardecl.getId().getName(),
//                            md.getName() + Constants.SEPARATOR + vardecl.getId().getName() + 0);
    }

    private void addFieldsOfCurrentClass(Map<Variable, Integer> variables, Set<String> fields, ClassOrInterfaceDeclaration typeDeclaration) {
        for (BodyDeclaration bd : typeDeclaration.getMembers()) {
            if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = (FieldDeclaration) bd;
                for (VariableDeclarator vardecl : fd.getVariables()) {
                    String name = vardecl.getId().getName();
                    addField(variables, fields, name);
                }
            }
        }
    }

    private void addFieldsOfParentClass(Map<Variable, Integer> variables, Set<String> fields, ClassOrInterfaceDeclaration typeDeclaration) {
        List<ClassOrInterfaceType> extendTypes = typeDeclaration.getExtends();
        if (extendTypes != null) {
            for (ClassOrInterfaceType classOrInterfaceType : extendTypes) {
                ClassType type = imports.getClassByShortName(classOrInterfaceType.getName());
                try {
                    String className = type.toString();
                    Integer endOfClassNameIndex = className.indexOf("<");
                    if (endOfClassNameIndex == -1) {
                        endOfClassNameIndex = className.length();
                    }
                    Class clazz = Class.forName(className.substring(0, endOfClassNameIndex));
                    Field[] fieldsOfClass = clazz.getDeclaredFields();
                    for (Field field : fieldsOfClass) {
                        if (!Modifier.isPrivate(field.getModifiers())) {
                            addField(variables, fields, field.getName());
                        }
                    }
                } catch (ClassNotFoundException e) {
                    log.error(e);
                }
            }
        }
    }
}

