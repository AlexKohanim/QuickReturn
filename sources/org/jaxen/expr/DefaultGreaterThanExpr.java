package org.jaxen.expr;

class DefaultGreaterThanExpr extends DefaultRelationalExpr {
    private static final long serialVersionUID = 6379252220540222867L;

    DefaultGreaterThanExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String getOperator() {
        return ">";
    }

    /* access modifiers changed from: protected */
    public boolean evaluateDoubleDouble(Double lhs, Double rhs) {
        return lhs.compareTo(rhs) > 0;
    }
}
