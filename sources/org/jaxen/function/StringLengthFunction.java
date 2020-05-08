package org.jaxen.function;

import java.util.List;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

public class StringLengthFunction implements Function {
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.size() == 0) {
            return evaluate(context.getNodeSet(), context.getNavigator());
        }
        if (args.size() == 1) {
            return evaluate(args.get(0), context.getNavigator());
        }
        throw new FunctionCallException("string-length() requires one argument.");
    }

    public static Double evaluate(Object obj, Navigator nav) throws FunctionCallException {
        String str = StringFunction.evaluate(obj, nav);
        char[] data = str.toCharArray();
        int length = 0;
        int i = 0;
        while (i < data.length) {
            length++;
            if (data[i] >= 55296) {
                try {
                    char low = data[i + 1];
                    if (low < 56320 || low > 57343) {
                        throw new FunctionCallException("Bad surrogate pair in string " + str);
                    }
                    i++;
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new FunctionCallException("Bad surrogate pair in string " + str);
                }
            }
            i++;
        }
        return new Double((double) length);
    }
}
