package org.jaxen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Context implements Serializable {
    private static final long serialVersionUID = 2315979994685591055L;
    private ContextSupport contextSupport;
    private List nodeSet = Collections.EMPTY_LIST;
    private int position = 0;
    private int size = 0;

    public Context(ContextSupport contextSupport2) {
        this.contextSupport = contextSupport2;
    }

    public void setNodeSet(List nodeSet2) {
        this.nodeSet = nodeSet2;
        this.size = nodeSet2.size();
        if (this.position >= this.size) {
            this.position = 0;
        }
    }

    public List getNodeSet() {
        return this.nodeSet;
    }

    public void setContextSupport(ContextSupport contextSupport2) {
        this.contextSupport = contextSupport2;
    }

    public ContextSupport getContextSupport() {
        return this.contextSupport;
    }

    public Navigator getNavigator() {
        return getContextSupport().getNavigator();
    }

    public String translateNamespacePrefixToUri(String prefix) {
        return getContextSupport().translateNamespacePrefixToUri(prefix);
    }

    public Object getVariableValue(String namespaceURI, String prefix, String localName) throws UnresolvableException {
        return getContextSupport().getVariableValue(namespaceURI, prefix, localName);
    }

    public Function getFunction(String namespaceURI, String prefix, String localName) throws UnresolvableException {
        return getContextSupport().getFunction(namespaceURI, prefix, localName);
    }

    public void setSize(int size2) {
        this.size = size2;
    }

    public int getSize() {
        return this.size;
    }

    public void setPosition(int position2) {
        this.position = position2;
    }

    public int getPosition() {
        return this.position;
    }

    public Context duplicate() {
        Context dupe = new Context(getContextSupport());
        List thisNodeSet = getNodeSet();
        if (thisNodeSet != null) {
            List dupeNodeSet = new ArrayList(thisNodeSet.size());
            dupeNodeSet.addAll(thisNodeSet);
            dupe.setNodeSet(dupeNodeSet);
            dupe.setPosition(this.position);
        }
        return dupe;
    }
}
