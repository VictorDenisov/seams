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
        val classProcessor = new ClassProcessor(typeDeclaration)
        classProcessor.compute()
        outData(classProcessor)
    }

    private[seams] def outData(classProcessor: ClassProcessor) {
        ScalaApp.printFields(classProcessor.fields)
        ScalaApp.printDeps("Dependencies", classProcessor.dependencies)
        ScalaApp.printDeps("UponType", classProcessor.dependenciesUponType)
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
    
}

// vim: set ts=4 sw=4 et:
