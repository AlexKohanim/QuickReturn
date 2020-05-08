package org.jaxen.expr;

import java.util.Iterator;
import java.util.List;
import org.jaxen.Context;
import org.jaxen.JaxenException;
import org.jaxen.Navigator;
import org.jaxen.function.BooleanFunction;
import org.jaxen.function.NumberFunction;
import org.jaxen.function.StringFunction;

abstract class DefaultEqualityExpr extends DefaultTruthExpr implements EqualityExpr {
    /* access modifiers changed from: protected */
    public abstract boolean evaluateObjectObject(Object obj, Object obj2);

    DefaultEqualityExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String toString() {
        return "[(DefaultEqualityExpr): " + getLHS() + ", " + getRHS() + "]";
    }

    public Object evaluate(Context context) throws JaxenException {
        Object lhsValue = getLHS().evaluate(context);
        Object rhsValue = getRHS().evaluate(context);
        if (lhsValue == null || rhsValue == null) {
            return Boolean.FALSE;
        }
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

    private Boolean evaluateSetSet(List lhsSet, List rhsSet, Navigator nav) {
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
        if (eitherIsBoolean(lhs, rhs)) {
            return evaluateObjectObject(BooleanFunction.evaluate(lhs, nav), BooleanFunction.evaluate(rhs, nav));
        }
        if (eitherIsNumber(lhs, rhs)) {
            return evaluateObjectObject(NumberFunction.evaluate(lhs, nav), NumberFunction.evaluate(rhs, nav));
        }
        return evaluateObjectObject(StringFunction.evaluate(lhs, nav), StringFunction.evaluate(rhs, nav));
    }
}
