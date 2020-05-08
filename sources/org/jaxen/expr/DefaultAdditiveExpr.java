package org.jaxen.expr;

abstract class DefaultAdditiveExpr extends DefaultArithExpr implements AdditiveExpr {
    DefaultAdditiveExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String toString() {
        return "[(" + getClass().getName() + "): " + getLHS() + ", " + getRHS() + "]";
    }
}
