package org.jaxen.function;

import java.util.List;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

public class SubstringBeforeFunction implements Function {
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.size() == 2) {
            return evaluate(args.get(0), args.get(1), context.getNavigator());
        }
        throw new FunctionCallException("substring-before() requires two arguments.");
    }

    public static String evaluate(Object strArg, Object matchArg, Navigator nav) {
        String str = StringFunction.evaluate(strArg, nav);
        int loc = str.indexOf(StringFunction.evaluate(matchArg, nav));
        if (loc < 0) {
            return "";
        }
        return str.substring(0, loc);
    }
}
