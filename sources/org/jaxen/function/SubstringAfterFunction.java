package org.jaxen.function;

import java.util.List;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

public class SubstringAfterFunction implements Function {
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.size() == 2) {
            return evaluate(args.get(0), args.get(1), context.getNavigator());
        }
        throw new FunctionCallException("substring-after() requires two arguments.");
    }

    public static String evaluate(Object strArg, Object matchArg, Navigator nav) {
        String str = StringFunction.evaluate(strArg, nav);
        String match = StringFunction.evaluate(matchArg, nav);
        int loc = str.indexOf(match);
        if (loc < 0) {
            return "";
        }
        return str.substring(match.length() + loc);
    }
}
