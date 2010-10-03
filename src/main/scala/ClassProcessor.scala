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
        val fields = ScalaApp.findFields(typeDeclaration)
        val (deps, uponType) = ScalaApp.processMethods(typeDeclaration, fields)
        ScalaApp.printFields(fields)
        ScalaApp.printDeps("Dependencies", deps)
        ScalaApp.printDeps("UponType", uponType)
    }
}
