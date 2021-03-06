package org.creativelabs;

import japa.parser.ast.body.*;
import japa.parser.ast.stmt.*;

import org.creativelabs.report.*;
import org.creativelabs.ssa.SimpleSsaForm;

import java.util.*;

class ClassProcessor {
    private ClassOrInterfaceDeclaration typeDeclaration;

    protected Map<String, Collection<Dependency>> dependencies = new HashMap<String, Collection<Dependency>>();

    private DependencyCounterVisitorBuilder dependencyCounterBuilder;

    protected HashMap<String, InternalInstancesGraph> internalInstances
            = new HashMap<String, InternalInstancesGraph>();
    protected Set<SimpleSsaForm> forms = new HashSet<SimpleSsaForm>();

    ClassProcessor(ClassOrInterfaceDeclaration typeDeclaration,
                   DependencyCounterVisitorBuilder dependencyCounterBuilder) {
        this.typeDeclaration = typeDeclaration;
        this.dependencyCounterBuilder = dependencyCounterBuilder;
    }

    public void buildReport(ReportBuilder reportBuilder) {
        reportBuilder.setDependencies(typeDeclaration.getName(), dependencies);
        reportBuilder.setInternalInstances(typeDeclaration.getName(), internalInstances);
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

    public Map<String, Collection<Dependency>> getDependencies() {
        return dependencies;
    }

    public Set<SimpleSsaForm> getForms() {
        return forms;
    }

    public void compute() {
        for (BodyDeclaration bd : typeDeclaration.getMembers()) {
            if (bd instanceof MethodDeclaration) {
                MethodDeclaration md = (MethodDeclaration) bd;
                findOutgoingDependencies(md);
                //findSsaForm(md);
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
        SsaFormBuilderVisitor ssaFormBuilderVisitor = new SsaFormBuilderVisitor();
        SimpleSsaForm form = new SimpleSsaForm(md.getName(),
                ssaFormBuilderVisitor.visit(md, new VariablesHolder(new HashMap<String, Integer>())));
        forms.add(form);
    }

}

