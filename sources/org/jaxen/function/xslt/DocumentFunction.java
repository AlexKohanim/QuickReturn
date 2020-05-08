package org.jaxen.function.xslt;

import java.util.List;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.StringFunction;

public class DocumentFunction implements Function {
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.size() == 1) {
            Navigator nav = context.getNavigator();
            return evaluate(StringFunction.evaluate(args.get(0), nav), nav);
        }
        throw new FunctionCallException("document() requires one argument.");
    }

    public static Object evaluate(String url, Navigator nav) throws FunctionCallException {
        return nav.getDocument(url);
    }
}
