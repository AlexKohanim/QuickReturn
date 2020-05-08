package org.jaxen.expr;

import java.util.Iterator;
import java.util.List;
import org.jaxen.Context;
import org.jaxen.JaxenException;
import org.jaxen.Navigator;
import org.jaxen.function.NumberFunction;

abstract class DefaultRelationalExpr extends DefaultTruthExpr implements RelationalExpr {
    /* access modifiers changed from: protected */
    public abstract boolean evaluateDoubleDouble(Double d, Double d2);

    DefaultRelationalExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String toString() {
        return "[(DefaultRelationalExpr): " + getLHS() + ", " + getRHS() + "]";
    }

    public Object evaluate(Context context) throws JaxenException {
        Object lhsValue = getLHS().evaluate(context);
        Object rhsValue = getRHS().evaluate(context);
        Navigator nav = context.getNavigator();
        if (bothAreSets(lhsValue, rhsValue)) {
            return evaluateSetSet((List) lhsValue, (List) rhsValue, nav);
        }
        if (!eitherIsSet(lhsValue, rhsValue)) {
            return evaluateObjectObject(lhsValue, rhsValue, nav) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (isSet(lhsValue)) {
            return evaluateSetSet((List) lhsValue, convertToList(rhsValue), nav);
        }
        return evaluateSetSet(convertToList(lhsValue), (List) rhsValue, nav);
    }

    private Object evaluateSetSet(List lhsSet, List rhsSet, Navigator nav) {
        if (setIsEmpty(lhsSet) || setIsEmpty(rhsSet)) {
            return Boolean.FALSE;
        }
        for (Object lhs : lhsSet) {
            Iterator rhsIterator = rhsSet.iterator();
            while (true) {
                if (rhsIterator.hasNext()) {
                    if (evaluateObjectObject(lhs, rhsIterator.next(), nav)) {
                        return Boolean.TRUE;
                    }
                }
            }
        }
        return Boolean.FALSE;
    }

    private boolean evaluateObjectObject(Object lhs, Object rhs, Navigator nav) {
        if (lhs == null || rhs == null) {
            return false;
        }
        Double lhsNum = NumberFunction.evaluate(lhs, nav);
        Double rhsNum = NumberFunction.evaluate(rhs, nav);
        if (NumberFunction.isNaN(lhsNum) || NumberFunction.isNaN(rhsNum)) {
            return false;
        }
        return evaluateDoubleDouble(lhsNum, rhsNum);
    }
}
