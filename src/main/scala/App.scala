package org.creativelabs.seams

import java.io._
import japa.parser.ast._
import japa.parser.ast.visitor._
import japa.parser.ast.body._
import japa.parser.ast.`type`._
import japa.parser._
import scala.collection.JavaConversions._
import scala.collection._

object ScalaApp {

    def main(args: Array[String]) {
        val fis = new FileInputStream(new File("Sample.java"))

        val cu = JavaParser.parse(fis)
        for (typeDeclaration <- cu.getTypes()) {
            typeDeclaration match {
                case v:ClassOrInterfaceDeclaration => processClass(v)
            }
        }
    }

    private[seams] def printFields(fields: Map[String, String]) {
        for ((key, value) <- fields) {
            println(key + " -> " + value)
        }
    }

    private[seams] def processClass(typeDeclaration: ClassOrInterfaceDeclaration) {
        val fields = findFields(typeDeclaration)
        val (deps, uponType) = processMethods(typeDeclaration, fields)
        printFields(fields)
        printDeps("Dependencies", deps)
        printDeps("UponType", uponType)
    }

    private[seams] def outputSet(depsName: String, set: Set[String]) {
        println(depsName + "(")
        for (value <- set) {
            println(value)
        }
        println(")")
    }

    private[seams] def printDeps(depsName: String, deps: Map[String, Set[String]]) {
        for ((key, value) <- deps) {
            print(key + " -> ")
            outputSet(depsName, value)
        }
    }
    
    private class TypeVisitor extends VoidVisitorAdapter[Object] {
        var name: String = _

        override def visit(n: ClassOrInterfaceType, a: Object) = name = n.getName()

        override def visit(n: PrimitiveType, a: Object) = name = n.getType().toString()
    }
    
    private[seams] def findFields(typeDeclaration: ClassOrInterfaceDeclaration): 
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

    private[seams] def processMethods(n: ClassOrInterfaceDeclaration, 
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
}

// vim: set ts=4 sw=4 et:
