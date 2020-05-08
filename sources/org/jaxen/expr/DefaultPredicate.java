package org.jaxen.expr;

import org.jaxen.Context;
import org.jaxen.JaxenException;

class DefaultPredicate implements Predicate {
    private static final long serialVersionUID = -4140068594075364971L;
    private Expr expr;

    DefaultPredicate(Expr expr2) {
        setExpr(expr2);
    }

    public Expr getExpr() {
        return this.expr;
    }

    public void setExpr(Expr expr2) {
        this.expr = expr2;
    }

    public String getText() {
        return "[" + getExpr().getText() + "]";
    }

    public String toString() {
        return "[(DefaultPredicate): " + getExpr() + "]";
    }

    public void simplify() {
        setExpr(getExpr().simplify());
    }

    public Object evaluate(Context context) throws JaxenException {
        return getExpr().evaluate(context);
    }
}
