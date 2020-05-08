package org.jaxen.function;

import java.util.List;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;

public class ConcatFunction implements Function {
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.size() >= 2) {
            return evaluate(args, context.getNavigator());
        }
        throw new FunctionCallException("concat() requires at least two arguments");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.List, code=java.util.List<java.lang.Object>, for r3v0, types: [java.util.List, java.util.List<java.lang.Object>] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String evaluate(java.util.List<java.lang.Object> r3, org.jaxen.Navigator r4) {
        /*
            java.lang.StringBuffer r1 = new java.lang.StringBuffer
            r1.<init>()
            java.util.Iterator r0 = r3.iterator()
        L_0x0009:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L_0x001b
            java.lang.Object r2 = r0.next()
            java.lang.String r2 = org.jaxen.function.StringFunction.evaluate(r2, r4)
            r1.append(r2)
            goto L_0x0009
        L_0x001b:
            java.lang.String r2 = r1.toString()
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jaxen.function.ConcatFunction.evaluate(java.util.List, org.jaxen.Navigator):java.lang.String");
    }
}
