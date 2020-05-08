package org.jaxen.function;

import java.util.List;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

public class NormalizeSpaceFunction implements Function {
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.size() == 0) {
            return evaluate(context.getNodeSet(), context.getNavigator());
        }
        if (args.size() == 1) {
            return evaluate(args.get(0), context.getNavigator());
        }
        throw new FunctionCallException("normalize-space() cannot have more than one argument");
    }

    public static String evaluate(Object strArg, Navigator nav) {
        char[] buffer = StringFunction.evaluate(strArg, nav).toCharArray();
        int write = 0;
        int lastWrite = 0;
        boolean wroteOne = false;
        int read = 0;
        while (read < buffer.length) {
            if (isXMLSpace(buffer[read])) {
                if (wroteOne) {
                    int write2 = write + 1;
                    buffer[write] = ' ';
                    write = write2;
                }
                do {
                    read++;
                    if (read >= buffer.length) {
                        break;
                    }
                } while (isXMLSpace(buffer[read]));
            } else {
                int write3 = write + 1;
                int read2 = read + 1;
                buffer[write] = buffer[read];
                wroteOne = true;
                lastWrite = write3;
                read = read2;
                write = write3;
            }
        }
        return new String(buffer, 0, lastWrite);
    }

    private static boolean isXMLSpace(char c) {
        return c == ' ' || c == 10 || c == 13 || c == 9;
    }
}
