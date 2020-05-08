package org.jaxen.expr;

import org.jaxen.Context;

class DefaultLiteralExpr extends DefaultExpr implements LiteralExpr {
    private static final long serialVersionUID = -953829179036273338L;
    private String literal;

    DefaultLiteralExpr(String literal2) {
        this.literal = literal2;
    }

    public String getLiteral() {
        return this.literal;
    }

    public String toString() {
        return "[(DefaultLiteralExpr): " + getLiteral() + "]";
    }

    public String getText() {
        if (this.literal.indexOf(34) == -1) {
            return "\"" + getLiteral() + "\"";
        }
        return "'" + getLiteral() + "'";
    }

    public Object evaluate(Context context) {
        return getLiteral();
    }
}
