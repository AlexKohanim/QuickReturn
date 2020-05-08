package org.jaxen;

import java.io.Serializable;
import java.util.List;
import org.jaxen.expr.Expr;
import org.jaxen.expr.XPathExpr;
import org.jaxen.function.BooleanFunction;
import org.jaxen.function.NumberFunction;
import org.jaxen.function.StringFunction;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.saxpath.XPathReader;
import org.jaxen.saxpath.XPathSyntaxException;
import org.jaxen.saxpath.helpers.XPathReaderFactory;
import org.jaxen.util.SingletonList;

public class BaseXPath implements XPath, Serializable {
    private final String exprText;
    private Navigator navigator;
    private ContextSupport support;
    private final XPathExpr xpath;

    protected BaseXPath(String xpathExpr) throws JaxenException {
        try {
            XPathReader reader = XPathReaderFactory.createReader();
            JaxenHandler handler = new JaxenHandler();
            reader.setXPathHandler(handler);
            reader.parse(xpathExpr);
            this.xpath = handler.getXPathExpr();
            this.exprText = xpathExpr;
        } catch (XPathSyntaxException e) {
            throw new XPathSyntaxException(e);
        } catch (SAXPathException e2) {
            throw new JaxenException((Throwable) e2);
        }
    }

    public BaseXPath(String xpathExpr, Navigator navigator2) throws JaxenException {
        this(xpathExpr);
        this.navigator = navigator2;
    }

    public Object evaluate(Object context) throws JaxenException {
        List answer = selectNodes(context);
        if (answer != null && answer.size() == 1) {
            Object first = answer.get(0);
            if ((first instanceof String) || (first instanceof Number) || (first instanceof Boolean)) {
                return first;
            }
        }
        return answer;
    }

    public List selectNodes(Object node) throws JaxenException {
        return selectNodesForContext(getContext(node));
    }

    public Object selectSingleNode(Object node) throws JaxenException {
        List results = selectNodes(node);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    public String valueOf(Object node) throws JaxenException {
        return stringValueOf(node);
    }

    public String stringValueOf(Object node) throws JaxenException {
        Context context = getContext(node);
        Object result = selectSingleNodeForContext(context);
        if (result == null) {
            return "";
        }
        return StringFunction.evaluate(result, context.getNavigator());
    }

    public boolean booleanValueOf(Object node) throws JaxenException {
        Context context = getContext(node);
        List result = selectNodesForContext(context);
        if (result == null) {
            return false;
        }
        return BooleanFunction.evaluate(result, context.getNavigator()).booleanValue();
    }

    public Number numberValueOf(Object node) throws JaxenException {
        Context context = getContext(node);
        return NumberFunction.evaluate(selectSingleNodeForContext(context), context.getNavigator());
    }

    public void addNamespace(String prefix, String uri) throws JaxenException {
        NamespaceContext nsContext = getNamespaceContext();
        if (nsContext instanceof SimpleNamespaceContext) {
            ((SimpleNamespaceContext) nsContext).addNamespace(prefix, uri);
            return;
        }
        throw new JaxenException("Operation not permitted while using a non-simple namespace context.");
    }

    public void setNamespaceContext(NamespaceContext namespaceContext) {
        getContextSupport().setNamespaceContext(namespaceContext);
    }

    public void setFunctionContext(FunctionContext functionContext) {
        getContextSupport().setFunctionContext(functionContext);
    }

    public void setVariableContext(VariableContext variableContext) {
        getContextSupport().setVariableContext(variableContext);
    }

    public NamespaceContext getNamespaceContext() {
        return getContextSupport().getNamespaceContext();
    }

    public FunctionContext getFunctionContext() {
        return getContextSupport().getFunctionContext();
    }

    public VariableContext getVariableContext() {
        return getContextSupport().getVariableContext();
    }

    public Expr getRootExpr() {
        return this.xpath.getRootExpr();
    }

    public String toString() {
        return this.exprText;
    }

    public String debug() {
        return this.xpath.toString();
    }

    /* access modifiers changed from: protected */
    public Context getContext(Object node) {
        if (node instanceof Context) {
            return (Context) node;
        }
        Context fullContext = new Context(getContextSupport());
        if (node instanceof List) {
            fullContext.setNodeSet((List) node);
        } else {
            fullContext.setNodeSet(new SingletonList(node));
        }
        return fullContext;
    }

    /* access modifiers changed from: protected */
    public ContextSupport getContextSupport() {
        if (this.support == null) {
            this.support = new ContextSupport(createNamespaceContext(), createFunctionContext(), createVariableContext(), getNavigator());
        }
        return this.support;
    }

    public Navigator getNavigator() {
        return this.navigator;
    }

    /* access modifiers changed from: protected */
    public FunctionContext createFunctionContext() {
        return XPathFunctionContext.getInstance();
    }

    /* access modifiers changed from: protected */
    public NamespaceContext createNamespaceContext() {
        return new SimpleNamespaceContext();
    }

    /* access modifiers changed from: protected */
    public VariableContext createVariableContext() {
        return new SimpleVariableContext();
    }

    /* access modifiers changed from: protected */
    public List selectNodesForContext(Context context) throws JaxenException {
        return this.xpath.asList(context);
    }

    /* access modifiers changed from: protected */
    public Object selectSingleNodeForContext(Context context) throws JaxenException {
        List results = selectNodesForContext(context);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }
}
