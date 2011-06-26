package org.creativelabs;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.BlockStmt;
import org.creativelabs.graph.condition.EmptyCondition;
import org.creativelabs.iig.ConditionInternalInstancesGraph;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.report.ReportBuilder;
import org.creativelabs.ssa.SsaError;
import org.creativelabs.ssa.holder.SimpleMultiHolder;
import org.creativelabs.ssa.holder.SimpleMultiHolderBuilder;
import org.creativelabs.ssa.holder.variable.StringVariable;
import org.creativelabs.ssa.holder.variable.Variable;
import org.creativelabs.ssa.representation.AstSsaFormRepresentation;
import org.creativelabs.ssa.representation.SsaFormRepresentation;
import org.creativelabs.ssa.visitor.SsaFormConverter;
import org.creativelabs.typefinder.DependenciesChart;
import org.creativelabs.typefinder.Dependency;
import org.creativelabs.typefinder.DependencyCounterVisitor;
import org.creativelabs.typefinder.DependencyCounterVisitorBuilder;

import java.util.*;

public class ClassProcessor {

    private ClassOrInterfaceDeclaration typeDeclaration;

    public static String debugInfo;

    protected Map<String, Collection<Dependency>> dependencies = new HashMap<String, Collection<Dependency>>();

    private SimpleMultiHolderBuilder holderBuilder;

    private DependencyCounterVisitorBuilder dependencyCounterBuilder;

    protected Map<String, InternalInstancesGraph> internalInstances
            = new HashMap<String, InternalInstancesGraph>();
    protected Set<SsaFormRepresentation> forms = new HashSet<SsaFormRepresentation>();

    protected Set<SsaError> errors = new HashSet<SsaError>();

    protected InternalInstancesGraph ssaInternalInstancesGraph = new ConditionInternalInstancesGraph();

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
        for (BodyDeclaration bd : typeDeclaration.getMembers()) {
            if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = (FieldDeclaration) bd;
                for (VariableDeclarator vardecl : fd.getVariables()) {
                    String name = vardecl.getId().getName();

                    variables.put(new StringVariable(name, Constants.THIS_SCOPE), 0);
                    fields.add(vardecl.getId().getName());

//                    ssaInternalInstancesGraph.addEdge(vardecl.getId().getName(),
//                            md.getName() + Constants.SEPARATOR + vardecl.getId().getName() + 0);

                }
            }
        }

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
}

