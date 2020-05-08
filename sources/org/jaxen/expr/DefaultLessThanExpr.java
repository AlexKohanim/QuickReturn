package org.jaxen.expr;

class DefaultLessThanExpr extends DefaultRelationalExpr {
    private static final long serialVersionUID = 8423816025305001283L;

    DefaultLessThanExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String getOperator() {
        return "<";
    }

    /* access modifiers changed from: protected */
    public boolean evaluateDoubleDouble(Double lhs, Double rhs) {
        return lhs.compareTo(rhs) < 0;
    }
}
