package org.jaxen.expr;

import org.jaxen.Context;
import org.jaxen.JaxenException;
import org.jaxen.Navigator;
import org.jaxen.function.BooleanFunction;

class DefaultOrExpr extends DefaultLogicalExpr {
    private static final long serialVersionUID = 4894552680753026730L;

    DefaultOrExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String getOperator() {
        return "or";
    }

    public String toString() {
        return "[(DefaultOrExpr): " + getLHS() + ", " + getRHS() + "]";
    }

    public Object evaluate(Context context) throws JaxenException {
        Navigator nav = context.getNavigator();
        if (BooleanFunction.evaluate(getLHS().evaluate(context), nav).booleanValue()) {
            return Boolean.TRUE;
        }
        if (BooleanFunction.evaluate(getRHS().evaluate(context), nav).booleanValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
