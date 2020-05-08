package org.jaxen.expr;

class DefaultLessThanEqualExpr extends DefaultRelationalExpr {
    private static final long serialVersionUID = 7980276649555334242L;

    DefaultLessThanEqualExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String getOperator() {
        return "<=";
    }

    /* access modifiers changed from: protected */
    public boolean evaluateDoubleDouble(Double lhs, Double rhs) {
        return lhs.compareTo(rhs) <= 0;
    }
}
