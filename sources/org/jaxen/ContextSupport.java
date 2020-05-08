package org.jaxen;

import java.io.Serializable;

public class ContextSupport implements Serializable {
    private static final long serialVersionUID = 4494082174713652559L;
    private transient FunctionContext functionContext;
    private NamespaceContext namespaceContext;
    private Navigator navigator;
    private VariableContext variableContext;

    public ContextSupport() {
    }

    public ContextSupport(NamespaceContext namespaceContext2, FunctionContext functionContext2, VariableContext variableContext2, Navigator navigator2) {
        setNamespaceContext(namespaceContext2);
        setFunctionContext(functionContext2);
        setVariableContext(variableContext2);
        this.navigator = navigator2;
    }

    public void setNamespaceContext(NamespaceContext namespaceContext2) {
        this.namespaceContext = namespaceContext2;
    }

    public NamespaceContext getNamespaceContext() {
        return this.namespaceContext;
    }

    public void setFunctionContext(FunctionContext functionContext2) {
        this.functionContext = functionContext2;
    }

    public FunctionContext getFunctionContext() {
        return this.functionContext;
    }

    public void setVariableContext(VariableContext variableContext2) {
        this.variableContext = variableContext2;
    }

    public VariableContext getVariableContext() {
        return this.variableContext;
    }

    public Navigator getNavigator() {
        return this.navigator;
    }

    public String translateNamespacePrefixToUri(String prefix) {
        if ("xml".equals(prefix)) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        NamespaceContext context = getNamespaceContext();
        if (context != null) {
            return context.translateNamespacePrefixToUri(prefix);
        }
        return null;
    }

    public Object getVariableValue(String namespaceURI, String prefix, String localName) throws UnresolvableException {
        VariableContext context = getVariableContext();
        if (context != null) {
            return context.getVariableValue(namespaceURI, prefix, localName);
        }
        throw new UnresolvableException("No variable context installed");
    }

    public Function getFunction(String namespaceURI, String prefix, String localName) throws UnresolvableException {
        FunctionContext context = getFunctionContext();
        if (context != null) {
            return context.getFunction(namespaceURI, prefix, localName);
        }
        throw new UnresolvableException("No function context installed");
    }
}
