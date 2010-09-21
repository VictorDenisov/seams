package org.creativelabs.seams

import scala.collection.mutable._

import japa.parser.ast.stmt._
import japa.parser.ast.expr._
import japa.parser.ast.visitor._
import org.creativelabs.ScopeDetectorVisitor
import scala.collection.JavaConversions._

private class DependencyCounterVisitor(private val classFields: Map[String, String]) 
                    extends VoidVisitorAdapter[Object] {

    private var dependencies = new HashSet[String]

    private val localVariables = new HashMap[String, String]

    private val internalInstances = new HashMap[String, Boolean]

    def getDependencies = dependencies

    override def visit(n: BlockStmt, o: Object) {
        for (statement <- n.getStmts) {
            statement.accept(this, o)
        }
    }

    override def visit(n: NameExpr, o: Object) {
        dependencies += n.getName()
    }
    
    override def visit(n: MethodCallExpr, o: Object) {
        val scopeDetector = new ScopeDetectorVisitor
        scopeDetector.visit(n, o)
        dependencies += n.toString
        super.visit(n, o);
    }

    override def visit(n: FieldAccessExpr, o: Object) {
        val scopeDetector = new ScopeDetectorVisitor
        scopeDetector.visit(n, o)
        dependencies += n.toString
        super.visit(n, o)
    }

    override def visit(n: AssignExpr, o: Object) {
        if (n.getValue() != null) {
            val esv = new ExpressionSeparatorVisitor(internalInstances)
            n.getValue().accept(esv, null);
            if (esv.isAssignedInternalInstance) {
                internalInstances += (n.getTarget.toString -> true)
            }
        }
        super.visit(n, o)
    }

    override def visit(n: VariableDeclarationExpr, o: Object) {
        for (v <- n.getVars) {
            localVariables += (v.getId.getName -> n.getType.toString)
            val esv = new ExpressionSeparatorVisitor(internalInstances)
            if (v.getInit != null) {
                v.getInit.accept(esv, null)
                if (esv.isAssignedInternalInstance) {
                    internalInstances += (v.getId.getName -> true)
                }
            }
        }
        super.visit(n, o)
    }
}


// vim: set ts=4 sw=4 et:
