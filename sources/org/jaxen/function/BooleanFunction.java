package org.jaxen.function;

import java.util.List;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

public class BooleanFunction implements Function {
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.size() == 1) {
            return evaluate(args.get(0), context.getNavigator());
        }
        throw new FunctionCallException("boolean() requires one argument");
    }

    public static Boolean evaluate(Object obj, Navigator nav) {
        if (obj instanceof List) {
            List list = (List) obj;
            if (list.size() == 0) {
                return Boolean.FALSE;
            }
            obj = list.get(0);
        }
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        if (!(obj instanceof Number)) {
            return obj instanceof String ? ((String) obj).length() > 0 ? Boolean.TRUE : Boolean.FALSE : obj != null ? Boolean.TRUE : Boolean.FALSE;
        }
        double d = ((Number) obj).doubleValue();
        if (d == 0.0d || Double.isNaN(d)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
