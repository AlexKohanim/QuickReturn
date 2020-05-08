package org.jaxen.pattern;

import org.jaxen.Context;
import org.jaxen.Navigator;

public class NameTest extends NodeTest {
    private String name;
    private short nodeType;

    public NameTest(String name2, short nodeType2) {
        this.name = name2;
        this.nodeType = nodeType2;
    }

    public boolean matches(Object node, Context context) {
        Navigator navigator = context.getNavigator();
        if (this.nodeType == 1) {
            if (!navigator.isElement(node) || !this.name.equals(navigator.getElementName(node))) {
                return false;
            }
            return true;
        } else if (this.nodeType == 2) {
            if (!navigator.isAttribute(node) || !this.name.equals(navigator.getAttributeName(node))) {
                return false;
            }
            return true;
        } else if (navigator.isElement(node)) {
            return this.name.equals(navigator.getElementName(node));
        } else {
            if (navigator.isAttribute(node)) {
                return this.name.equals(navigator.getAttributeName(node));
            }
            return false;
        }
    }

    public double getPriority() {
        return 0.0d;
    }

    public short getMatchType() {
        return this.nodeType;
    }

    public String getText() {
        if (this.nodeType == 2) {
            return "@" + this.name;
        }
        return this.name;
    }

    public String toString() {
        return super.toString() + "[ name: " + this.name + " type: " + this.nodeType + " ]";
    }
}
