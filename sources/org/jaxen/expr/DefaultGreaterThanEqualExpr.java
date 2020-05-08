package org.jaxen.expr;

class DefaultGreaterThanEqualExpr extends DefaultRelationalExpr {
    private static final long serialVersionUID = -7848747981787197470L;

    DefaultGreaterThanEqualExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String getOperator() {
        return ">=";
    }

    /* access modifiers changed from: protected */
    public boolean evaluateDoubleDouble(Double lhs, Double rhs) {
        return lhs.compareTo(rhs) >= 0;
    }
}
