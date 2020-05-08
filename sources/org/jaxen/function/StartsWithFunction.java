package org.jaxen.function;

import java.util.List;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

public class StartsWithFunction implements Function {
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.size() == 2) {
            return evaluate(args.get(0), args.get(1), context.getNavigator());
        }
        throw new FunctionCallException("starts-with() requires two arguments.");
    }

    public static Boolean evaluate(Object strArg, Object matchArg, Navigator nav) {
        return StringFunction.evaluate(strArg, nav).startsWith(StringFunction.evaluate(matchArg, nav)) ? Boolean.TRUE : Boolean.FALSE;
    }
}
