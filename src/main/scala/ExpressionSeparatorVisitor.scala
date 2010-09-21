package org.creativelabs.seams

import japa.parser.ast.visitor._
import japa.parser.ast.expr._
import japa.parser.ast.stmt._
import scala.collection.mutable._
import org.creativelabs.ScopeDetectorVisitor

private class ExpressionSeparatorVisitor(private val internalInstances: Map[String, Boolean]) 
                extends VoidVisitorAdapter[Object] {

    private var assignedInternalInstance = false

    def isAssignedInternalInstance = assignedInternalInstance

    override def visit(n: NameExpr, o: Object) {
        val name = n.getName()
        if (internalInstances.contains(name)) {
            assignedInternalInstance = true;
        }
    }

    override def visit(n: FieldAccessExpr, o: Object) {
        val scopeDetector = new ScopeDetectorVisitor
        scopeDetector.visit(n, o)
        val name = scopeDetector.getName
        if (internalInstances.contains(name)) {
            assignedInternalInstance = true
        }
    }

    override def visit(n: MethodCallExpr, o: Object) {
        val scopeDetector = new ScopeDetectorVisitor
        scopeDetector.visit(n, o)
        val name = scopeDetector.getName
        if (internalInstances.contains(name)) {
            assignedInternalInstance = true
        }
    }

    override def visit(n: ExplicitConstructorInvocationStmt, o: Object) {
        println("Processing constructor invocation")
    }

    override def visit(n: ObjectCreationExpr, o: Object) {
        assignedInternalInstance = true
        println("Construction of " + n.getType.getName)
    }

}

// vim: set ts=4 sw=4 et:
