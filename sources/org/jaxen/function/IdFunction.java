package org.jaxen.function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

public class IdFunction implements Function {
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.size() == 1) {
            return evaluate(context.getNodeSet(), args.get(0), context.getNavigator());
        }
        throw new FunctionCallException("id() requires one argument");
    }

    public static List evaluate(List contextNodes, Object arg, Navigator nav) {
        if (contextNodes.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        List nodes = new ArrayList();
        Object contextNode = contextNodes.get(0);
        if (arg instanceof List) {
            for (Object evaluate : (List) arg) {
                nodes.addAll(evaluate(contextNodes, StringFunction.evaluate(evaluate, nav), nav));
            }
            return nodes;
        }
        StringTokenizer tok = new StringTokenizer(StringFunction.evaluate(arg, nav), " \t\n\r");
        while (tok.hasMoreTokens()) {
            Object node = nav.getElementById(contextNode, tok.nextToken());
            if (node != null) {
                nodes.add(node);
            }
        }
        return nodes;
    }
}
