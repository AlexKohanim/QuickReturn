package org.jaxen.javabean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jaxen.BaseXPath;
import org.jaxen.Context;
import org.jaxen.JaxenException;

public class JavaBeanXPath extends BaseXPath {
    private static final long serialVersionUID = -1567521943360266313L;

    public JavaBeanXPath(String xpathExpr) throws JaxenException {
        super(xpathExpr, DocumentNavigator.getInstance());
    }

    /* access modifiers changed from: protected */
    public Context getContext(Object node) {
        if (node instanceof Context) {
            return (Context) node;
        }
        if (node instanceof Element) {
            return super.getContext(node);
        }
        if (!(node instanceof List)) {
            return super.getContext(new Element(null, "root", node));
        }
        List newList = new ArrayList();
        for (Object element : (List) node) {
            newList.add(new Element(null, "root", element));
        }
        return super.getContext(newList);
    }

    public Object evaluate(Object node) throws JaxenException {
        Object result = super.evaluate(node);
        if (result instanceof Element) {
            return ((Element) result).getObject();
        }
        if (!(result instanceof Collection)) {
            return result;
        }
        List newList = new ArrayList();
        for (Object member : (Collection) result) {
            if (member instanceof Element) {
                newList.add(((Element) member).getObject());
            } else {
                newList.add(member);
            }
        }
        return newList;
    }
}
