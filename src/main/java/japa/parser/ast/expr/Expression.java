package japa.parser.ast.expr;

import japa.parser.ast.Node;
import org.creativelabs.ssa.UMVariablesHolder;

/**
 * It's hacked class.
 * Has been added information about using, modifying variables into expressions.
 *
 * @author azotcsit
 *         Date: 24.04.11
 *         Time: 18:27
 */
public abstract class Expression extends Node {

    public Expression() {
    }

    public Expression(int beginLine, int beginColumn, int endLine, int endColumn) {
        super(beginLine, beginColumn, endLine, endColumn);
    }

    private UMVariablesHolder variablesHolder = new UMVariablesHolder();

    public UMVariablesHolder getVariablesHolder() {
        return variablesHolder;
    }
}
