package org.jaxen.function;

import java.util.List;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

public class RoundFunction implements Function {
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.size() == 1) {
            return evaluate(args.get(0), context.getNavigator());
        }
        throw new FunctionCallException("round() requires one argument.");
    }

    public static Double evaluate(Object obj, Navigator nav) {
        Double d = NumberFunction.evaluate(obj, nav);
        return (d.isNaN() || d.isInfinite()) ? d : new Double((double) Math.round(d.doubleValue()));
    }
}
