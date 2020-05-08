package org.jaxen.expr;

import org.jaxen.Context;
import org.jaxen.JaxenException;
import org.jaxen.Navigator;
import org.jaxen.function.BooleanFunction;

class DefaultAndExpr extends DefaultLogicalExpr {
    private static final long serialVersionUID = -5237984010263103742L;

    DefaultAndExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String getOperator() {
        return "and";
    }

    public String toString() {
        return "[(DefaultAndExpr): " + getLHS() + ", " + getRHS() + "]";
    }

    public Object evaluate(Context context) throws JaxenException {
        Navigator nav = context.getNavigator();
        if (!BooleanFunction.evaluate(getLHS().evaluate(context), nav).booleanValue()) {
            return Boolean.FALSE;
        }
        if (!BooleanFunction.evaluate(getRHS().evaluate(context), nav).booleanValue()) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
