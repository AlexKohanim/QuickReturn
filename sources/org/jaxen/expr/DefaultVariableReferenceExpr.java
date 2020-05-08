package org.jaxen.expr;

import org.jaxen.Context;
import org.jaxen.UnresolvableException;

class DefaultVariableReferenceExpr extends DefaultExpr implements VariableReferenceExpr {
    private static final long serialVersionUID = 8832095437149358674L;
    private String localName;
    private String prefix;

    DefaultVariableReferenceExpr(String prefix2, String variableName) {
        this.prefix = prefix2;
        this.localName = variableName;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getVariableName() {
        return this.localName;
    }

    public String toString() {
        return "[(DefaultVariableReferenceExpr): " + getQName() + "]";
    }

    private String getQName() {
        if ("".equals(this.prefix)) {
            return this.localName;
        }
        return this.prefix + ":" + this.localName;
    }

    public String getText() {
        return "$" + getQName();
    }

    public Object evaluate(Context context) throws UnresolvableException {
        return context.getVariableValue(context.translateNamespacePrefixToUri(getPrefix()), this.prefix, this.localName);
    }
}
