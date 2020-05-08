package org.jaxen.expr;

abstract class DefaultLogicalExpr extends DefaultTruthExpr implements LogicalExpr {
    DefaultLogicalExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }
}
