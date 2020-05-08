package org.jaxen.function;

import java.util.Iterator;
import java.util.List;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

public class NumberFunction implements Function {
    private static final Double NaN = new Double(Double.NaN);

    public Object call(Context context, List args) throws FunctionCallException {
        if (args.size() == 1) {
            return evaluate(args.get(0), context.getNavigator());
        }
        if (args.size() == 0) {
            return evaluate(context.getNodeSet(), context.getNavigator());
        }
        throw new FunctionCallException("number() takes at most one argument.");
    }

    public static Double evaluate(Object obj, Navigator nav) {
        if (obj instanceof Double) {
            return (Double) obj;
        }
        if (obj instanceof String) {
            try {
                return new Double((String) obj);
            } catch (NumberFormatException e) {
                return NaN;
            }
        } else if ((obj instanceof List) || (obj instanceof Iterator)) {
            return evaluate(StringFunction.evaluate(obj, nav), nav);
        } else {
            if (nav.isElement(obj) || nav.isAttribute(obj)) {
                return evaluate(StringFunction.evaluate(obj, nav), nav);
            }
            if (!(obj instanceof Boolean)) {
                return NaN;
            }
            if (obj == Boolean.TRUE) {
                return new Double(1.0d);
            }
            return new Double(0.0d);
        }
    }

    public static boolean isNaN(double val) {
        return Double.isNaN(val);
    }

    public static boolean isNaN(Double val) {
        return val.equals(NaN);
    }
}
