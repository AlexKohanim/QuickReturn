package org.jaxen.pattern;

import org.jaxen.Context;
import org.jaxen.Navigator;

public class NamespaceTest extends NodeTest {
    private short nodeType;
    private String prefix;

    public NamespaceTest(String prefix2, short nodeType2) {
        if (prefix2 == null) {
            prefix2 = "";
        }
        this.prefix = prefix2;
        this.nodeType = nodeType2;
    }

    public boolean matches(Object node, Context context) {
        Navigator navigator = context.getNavigator();
        String uri = getURI(node, context);
        if (this.nodeType == 1) {
            if (!navigator.isElement(node) || !uri.equals(navigator.getElementNamespaceUri(node))) {
                return false;
            }
            return true;
        } else if (this.nodeType != 2) {
            return false;
        } else {
            if (!navigator.isAttribute(node) || !uri.equals(navigator.getAttributeNamespaceUri(node))) {
                return false;
            }
            return true;
        }
    }

    public double getPriority() {
        return -0.25d;
    }

    public short getMatchType() {
        return this.nodeType;
    }

    public String getText() {
        return this.prefix + ":";
    }

    public String toString() {
        return super.toString() + "[ prefix: " + this.prefix + " type: " + this.nodeType + " ]";
    }

    /* access modifiers changed from: protected */
    public String getURI(Object node, Context context) {
        String uri = context.getNavigator().translateNamespacePrefixToUri(this.prefix, node);
        if (uri == null) {
            uri = context.getContextSupport().translateNamespacePrefixToUri(this.prefix);
        }
        if (uri == null) {
            return "";
        }
        return uri;
    }
}
