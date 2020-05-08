package org.jaxen.expr;

import org.jaxen.Context;
import org.jaxen.JaxenException;
import org.jaxen.function.NumberFunction;

class DefaultModExpr extends DefaultMultiplicativeExpr {
    private static final long serialVersionUID = -5554964716492040687L;

    DefaultModExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String getOperator() {
        return "mod";
    }

    public Object evaluate(Context context) throws JaxenException {
        return new Double(NumberFunction.evaluate(getLHS().evaluate(context), context.getNavigator()).doubleValue() % NumberFunction.evaluate(getRHS().evaluate(context), context.getNavigator()).doubleValue());
    }
}
