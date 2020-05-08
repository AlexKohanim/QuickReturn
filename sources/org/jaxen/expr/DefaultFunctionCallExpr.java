package org.jaxen.expr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jaxen.Context;
import org.jaxen.JaxenException;

public class DefaultFunctionCallExpr extends DefaultExpr implements FunctionCallExpr {
    private static final long serialVersionUID = -4747789292572193708L;
    private String functionName;
    private List parameters = new ArrayList();
    private String prefix;

    public DefaultFunctionCallExpr(String prefix2, String functionName2) {
        this.prefix = prefix2;
        this.functionName = functionName2;
    }

    public void addParameter(Expr parameter) {
        this.parameters.add(parameter);
    }

    public List getParameters() {
        return this.parameters;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getFunctionName() {
        return this.functionName;
    }

    public String getText() {
        StringBuffer buf = new StringBuffer();
        String prefix2 = getPrefix();
        if (prefix2 != null && prefix2.length() > 0) {
            buf.append(prefix2);
            buf.append(":");
        }
        buf.append(getFunctionName());
        buf.append("(");
        Iterator paramIter = getParameters().iterator();
        while (paramIter.hasNext()) {
            buf.append(((Expr) paramIter.next()).getText());
            if (paramIter.hasNext()) {
                buf.append(", ");
            }
        }
        buf.append(")");
        return buf.toString();
    }

    public Expr simplify() {
        List paramExprs = getParameters();
        int paramSize = paramExprs.size();
        List newParams = new ArrayList(paramSize);
        for (int i = 0; i < paramSize; i++) {
            newParams.add(((Expr) paramExprs.get(i)).simplify());
        }
        this.parameters = newParams;
        return this;
    }

    public String toString() {
        if (getPrefix() == null) {
            return "[(DefaultFunctionCallExpr): " + getFunctionName() + "(" + getParameters() + ") ]";
        }
        return "[(DefaultFunctionCallExpr): " + getPrefix() + ":" + getFunctionName() + "(" + getParameters() + ") ]";
    }

    public Object evaluate(Context context) throws JaxenException {
        return context.getFunction(context.translateNamespacePrefixToUri(getPrefix()), getPrefix(), getFunctionName()).call(context, evaluateParams(context));
    }

    public List evaluateParams(Context context) throws JaxenException {
        List paramExprs = getParameters();
        int paramSize = paramExprs.size();
        List paramValues = new ArrayList(paramSize);
        for (int i = 0; i < paramSize; i++) {
            paramValues.add(((Expr) paramExprs.get(i)).evaluate(context));
        }
        return paramValues;
    }
}
