package org.creativelabs;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.BlockStmt;
import org.creativelabs.graph.condition.StringCondition;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.iig.SimpleInternalInstancesGraph;
import org.creativelabs.report.ReportBuilder;
import org.creativelabs.ssa.SsaFormAstRepresentation;
import org.creativelabs.ssa.SsaFormConverter;
import org.creativelabs.ssa.VariablesHolder;

import java.util.*;

class ClassProcessor {
    private ClassOrInterfaceDeclaration typeDeclaration;

    protected Map<String, Collection<Dependency>> dependencies = new HashMap<String, Collection<Dependency>>();

    private DependencyCounterVisitorBuilder dependencyCounterBuilder;

    protected Map<String, InternalInstancesGraph> internalInstances
            = new HashMap<String, InternalInstancesGraph>();
    protected Set<SsaFormAstRepresentation> forms = new HashSet<SsaFormAstRepresentation>();

    protected InternalInstancesGraph ssaInternalInstancesGraph = new SimpleInternalInstancesGraph();

    ClassProcessor(ClassOrInterfaceDeclaration typeDeclaration,
                   DependencyCounterVisitorBuilder dependencyCounterBuilder) {
        this.typeDeclaration = typeDeclaration;
        this.dependencyCounterBuilder = dependencyCounterBuilder;
    }

    public void buildReport(ReportBuilder reportBuilder) {
        reportBuilder.setDependencies(typeDeclaration.getName(), dependencies);
        reportBuilder.setInternalInstances(typeDeclaration.getName(), internalInstances);
        reportBuilder.setSsaFormRepresentations(typeDeclaration.getName(), forms);
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

    public Set<SsaFormAstRepresentation> getForms() {
        return forms;
    }

    public void compute() {
        for (BodyDeclaration bd : typeDeclaration.getMembers()) {
            if (bd instanceof MethodDeclaration) {
                MethodDeclaration md = (MethodDeclaration) bd;
                findOutgoingDependencies(md);
                findSsaForm(md);
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

    void findSsaForm(MethodDeclaration md) {
        addUsingModifyingVariablesInformation(md);

        SsaFormConverter visitor = new SsaFormConverter(ssaInternalInstancesGraph);
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (BodyDeclaration bd : typeDeclaration.getMembers()) {
            if (bd instanceof FieldDeclaration) {
                FieldDeclaration fd = (FieldDeclaration) bd;
                for (VariableDeclarator vardecl : fd.getVariables()) {
                    map.put(vardecl.getId().getName(), 0);

                    visitor.addToGraph(vardecl.getId().getName(),
                            md.getName() + SsaFormConverter.SEPARATOR + vardecl.getId().getName() + 0);

//                    visitor.addToGraph(md.getName() + SsaFormConverter.SEPARATOR + vardecl.getId().getName(),
//                            vardecl.getId().getName() + SsaFormConverter.SEPARATOR + 0);
                }
            }
        }
        VariablesHolder holder = new VariablesHolder(map, new StringCondition("true"));
        visitor.visit(md, holder);
        SsaFormAstRepresentation form = new SsaFormAstRepresentation(md.getName(), md);
        forms.add(form);
        //TODO: To add graphs for all methods
        ssaInternalInstancesGraph = visitor.getGraph();
    }

    private void addUsingModifyingVariablesInformation(MethodDeclaration methodDeclaration) {
//        new UsingModifyingVariablesVisitor().visit(methodDeclaration.getBody());
    }

}

