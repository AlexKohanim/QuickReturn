package org.jaxen.expr;

abstract class DefaultBinaryExpr extends DefaultExpr implements BinaryExpr {
    private Expr lhs;
    private Expr rhs;

    public abstract String getOperator();

    DefaultBinaryExpr(Expr lhs2, Expr rhs2) {
        this.lhs = lhs2;
        this.rhs = rhs2;
    }

    public Expr getLHS() {
        return this.lhs;
    }

    public Expr getRHS() {
        return this.rhs;
    }

    public void setLHS(Expr lhs2) {
        this.lhs = lhs2;
    }

    public void setRHS(Expr rhs2) {
        this.rhs = rhs2;
    }

    public String getText() {
        return "(" + getLHS().getText() + " " + getOperator() + " " + getRHS().getText() + ")";
    }

    public String toString() {
        return "[" + getClass().getName() + ": " + getLHS() + ", " + getRHS() + "]";
    }

    public Expr simplify() {
        setLHS(getLHS().simplify());
        setRHS(getRHS().simplify());
        return this;
    }
}
