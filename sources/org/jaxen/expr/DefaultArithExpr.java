package org.jaxen.expr;

abstract class DefaultArithExpr extends DefaultBinaryExpr {
    DefaultArithExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String toString() {
        return "[(DefaultArithExpr): " + getLHS() + ", " + getRHS() + "]";
    }
}
