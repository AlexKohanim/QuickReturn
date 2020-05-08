package org.jaxen.expr;

abstract class DefaultMultiplicativeExpr extends DefaultArithExpr implements MultiplicativeExpr {
    DefaultMultiplicativeExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String toString() {
        return "[(DefaultMultiplicativeExpr): " + getLHS() + ", " + getRHS() + "]";
    }
}
