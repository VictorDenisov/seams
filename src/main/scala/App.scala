package org.creativelabs

import java.io._
import java.util._
import japa.parser.ast._
import japa.parser.ast.body._
import japa.parser.ast.`type`._
import japa.parser._
import scala.collection.JavaConversions._

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

    def processClass(typeDeclaration: ClassOrInterfaceDeclaration) {
        val vars = findFields(typeDeclaration)
        processMethods(typeDeclaration, vars)
    }
    
    class TypeVisitor extends VoidVisitorAdapter[Object] {
        val name: String

        def visit(n: ClassOrInterfaceType, a: Object) = name = n.getName()

        def visit(n: PrimitiveType, a: Object) = name = n.getType().toString()
    }
    
    def findFields(typeDeclaration: ClassOrInterfaceDeclaration): 
    HashMap[String, String] = {
        val fields = new HashMap[String, String]
        for (bd <- typeDeclaration.getMembers()) {
            bd match {
                case fd: FieldDeclaration => 
                for (vardecl <- fd.getVariables()) {
                    val tv = new TypeVisitor
                    fd.getType().accept(tv, null)
                    fields.put(vardecl.getId().getName(), tv.name)
                }
            }
        }
        fields
    }

    def processMethods(n: ClassOrInterfaceDeclaration, vars: Map[Stirng, String]) = {
        
    }
}

// vim: set ts=4 sw=4 et:
