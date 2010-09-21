package org.creativelabs.seams

import java.io._
import japa.parser.ast._
import japa.parser.ast.visitor._
import japa.parser.ast.body._
import japa.parser.ast.`type`._
import japa.parser._
import scala.collection.JavaConversions._
import scala.collection.mutable._

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

    def printFields(fields: Map[String, String]) {
        for ((key, value) <- fields) {
            println(key + " -> " + value)
        }
    }

    def processClass(typeDeclaration: ClassOrInterfaceDeclaration) {
        val fields = findFields(typeDeclaration)
        val deps = processMethods(typeDeclaration, fields)
        printFields(fields)
        printDeps(deps)
    }

    private def outputSet(set: Set[String]) {
        println("Dependencies(")
        for (value <- set) {
            println(value)
        }
        println(")")
    }

    private def printDeps(deps: Map[String, Set[String]]) {
        for ((key, value) <- deps) {
            print(key + " -> ")
            outputSet(value)
        }
    }
    
    private class TypeVisitor extends VoidVisitorAdapter[Object] {
        var name: String = _

        override def visit(n: ClassOrInterfaceType, a: Object) = name = n.getName()

        override def visit(n: PrimitiveType, a: Object) = name = n.getType().toString()
    }
    
    def findFields(typeDeclaration: ClassOrInterfaceDeclaration): 
                    Map[String, String] = {

        val fields = new HashMap[String, String]
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

    private def processMethods(n: ClassOrInterfaceDeclaration, 
                    classFields: Map[String, String]): Map[String, Set[String]] = {

        val outgoingDependencies = new HashMap[String, Set[String]]
        for (bd <- n.getMembers()) bd match {
            case md: MethodDeclaration =>
            outgoingDependencies += (md.getName -> findOutgoingDependencies(md, classFields))
            case _ => 
        }
        
        outgoingDependencies
    }

    private def findOutgoingDependencies(md: MethodDeclaration, 
                    classFields: Map[String, String]): Set[String] = {
        val body = md.getBody
        val dependencyCounter = new DependencyCounterVisitor(classFields)
        dependencyCounter.visit(body, null)
        dependencyCounter.getDependencies
    }
}

// vim: set ts=4 sw=4 et:
