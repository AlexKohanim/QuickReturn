package org.jaxen;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class SimpleNamespaceContext implements NamespaceContext, Serializable {
    private static final long serialVersionUID = -808928409643497762L;
    private Map namespaces;

    public SimpleNamespaceContext() {
        this.namespaces = new HashMap();
    }

    public SimpleNamespaceContext(Map namespaces2) {
        for (Entry entry : namespaces2.entrySet()) {
            if (entry.getKey() instanceof String) {
                if (!(entry.getValue() instanceof String)) {
                }
            }
            throw new ClassCastException("Non-string namespace binding");
        }
        this.namespaces = new HashMap(namespaces2);
    }

    public void addElementNamespaces(Navigator nav, Object element) throws UnsupportedAxisException {
        Iterator namespaceAxis = nav.getNamespaceAxisIterator(element);
        while (namespaceAxis.hasNext()) {
            Object namespace = namespaceAxis.next();
            String prefix = nav.getNamespacePrefix(namespace);
            String uri = nav.getNamespaceStringValue(namespace);
            if (translateNamespacePrefixToUri(prefix) == null) {
                addNamespace(prefix, uri);
            }
        }
    }

    public void addNamespace(String prefix, String URI) {
        this.namespaces.put(prefix, URI);
    }

    public String translateNamespacePrefixToUri(String prefix) {
        if (this.namespaces.containsKey(prefix)) {
            return (String) this.namespaces.get(prefix);
        }
        return null;
    }
}
