package org.jaxen.function;

import java.util.List;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

public class SumFunction implements Function {
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.size() == 1) {
            return evaluate(args.get(0), context.getNavigator());
        }
        throw new FunctionCallException("sum() requires one argument.");
    }

    public static Double evaluate(Object obj, Navigator nav) throws FunctionCallException {
        double sum = 0.0d;
        if (obj instanceof List) {
            for (Object evaluate : (List) obj) {
                sum += NumberFunction.evaluate(evaluate, nav).doubleValue();
            }
            return new Double(sum);
        }
        throw new FunctionCallException("The argument to the sum function must be a node-set");
    }
}
