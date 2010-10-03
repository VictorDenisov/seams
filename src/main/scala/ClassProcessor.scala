package org.creativelabs.seams

import java.io._
import japa.parser.ast._
import japa.parser.ast.visitor._
import japa.parser.ast.body._
import japa.parser.ast.`type`._
import japa.parser._
import scala.collection.JavaConversions._
import scala.collection._

class ClassProcessor(typeDeclaration: ClassOrInterfaceDeclaration) {
    private var fields: Map[String, String] = findFields(typeDeclaration)

    private var dependencies: Map[String, Set[String]] = new mutable.HashMap
    private var dependenciesUponType: Map[String, Set[String]] = new mutable.HashMap

    def compute() {            
        processMethods(typeDeclaration, fields)

        outData()
    }

    def outData() {
        ScalaApp.printFields(fields)
        ScalaApp.printDeps("Dependencies", dependencies)
        ScalaApp.printDeps("UponType", dependenciesUponType)
    }

    private def processMethods(n: ClassOrInterfaceDeclaration, 
                    classFields: Map[String, String]) {

        for (bd <- n.getMembers()) bd match {
            case md: MethodDeclaration =>
                val (deps, uponType) = findOutgoingDependencies(md, classFields)
                dependencies += (md.getName -> deps)
                dependenciesUponType += (md.getName -> uponType)
            case _ => 
        }
        
    }

    private def findOutgoingDependencies(md: MethodDeclaration, 
                    classFields: Map[String, String]): (Set[String], Set[String]) = {
        val body = md.getBody
        val dependencyCounter = new DependencyCounterVisitor(classFields)
        dependencyCounter.visit(body, null)
        (dependencyCounter.getDependencies, dependencyCounter.getDependenciesUponType)
    }

    private def findFields(typeDeclaration: ClassOrInterfaceDeclaration): 
                    Map[String, String] = {

        val fields = new mutable.HashMap[String, String]
        for (bd <- typeDeclaration.getMembers) bd match {
            case fd: FieldDeclaration => 
                for (vardecl <- fd.getVariables()) {
                    val tv = new TypeVisitor
                    fd.getType().accept(tv, null)
                    fields.put(vardecl.getId().getName(), tv.name)
                }
            case _ =>
        }
        fields
    }

    private class TypeVisitor extends VoidVisitorAdapter[Object] {
        var name: String = _

        override def visit(n: ClassOrInterfaceType, a: Object) = name = n.getName()

        override def visit(n: PrimitiveType, a: Object) = name = n.getType().toString()
    }

}
