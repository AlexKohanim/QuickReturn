package org.jaxen.function;

import java.util.List;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

public class FloorFunction implements Function {
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.size() == 1) {
            return evaluate(args.get(0), context.getNavigator());
        }
        throw new FunctionCallException("floor() requires one argument.");
    }

    public static Double evaluate(Object obj, Navigator nav) {
        return new Double(Math.floor(NumberFunction.evaluate(obj, nav).doubleValue()));
    }
}
