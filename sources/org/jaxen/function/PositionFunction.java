package org.jaxen.function;

import java.util.List;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;

public class PositionFunction implements Function {
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.size() == 0) {
            return evaluate(context);
        }
        throw new FunctionCallException("position() does not take any arguments.");
    }

    public static Double evaluate(Context context) {
        return new Double((double) context.getPosition());
    }
}
