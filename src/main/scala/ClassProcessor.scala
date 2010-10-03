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
    def compute() {            
        val fields = findFields(typeDeclaration)
        val (deps, uponType) = processMethods(typeDeclaration, fields)
        ScalaApp.printFields(fields)
        ScalaApp.printDeps("Dependencies", deps)
        ScalaApp.printDeps("UponType", uponType)
    }

    private def processMethods(n: ClassOrInterfaceDeclaration, 
                    classFields: Map[String, String]): 
                    (Map[String, Set[String]], Map[String, Set[String]]) = {

        val outgoingDependencies = new mutable.HashMap[String, Set[String]]
        val outgoingDependenciesUponType = new mutable.HashMap[String, Set[String]]

        for (bd <- n.getMembers()) bd match {
            case md: MethodDeclaration =>
                val (deps, uponType) = findOutgoingDependencies(md, classFields)
                outgoingDependencies += (md.getName -> deps)
                outgoingDependenciesUponType += (md.getName -> uponType)
            case _ => 
        }
        
        (outgoingDependencies, outgoingDependenciesUponType)
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
