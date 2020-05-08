package org.jaxen.function;

import java.util.List;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

public class NotFunction implements Function {
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.size() == 1) {
            return evaluate(args.get(0), context.getNavigator());
        }
        throw new FunctionCallException("not() requires one argument.");
    }

    public static Boolean evaluate(Object obj, Navigator nav) {
        return BooleanFunction.evaluate(obj, nav).booleanValue() ? Boolean.FALSE : Boolean.TRUE;
    }
}
