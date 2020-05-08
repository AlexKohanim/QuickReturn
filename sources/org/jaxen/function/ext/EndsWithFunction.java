package org.jaxen.function.ext;

import java.util.List;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.StringFunction;

public class EndsWithFunction implements Function {
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.size() == 2) {
            return evaluate(args.get(0), args.get(1), context.getNavigator());
        }
        throw new FunctionCallException("ends-with() requires two arguments.");
    }

    public static Boolean evaluate(Object strArg, Object matchArg, Navigator nav) {
        return StringFunction.evaluate(strArg, nav).endsWith(StringFunction.evaluate(matchArg, nav)) ? Boolean.TRUE : Boolean.FALSE;
    }
}
