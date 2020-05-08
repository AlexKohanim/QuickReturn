package org.jaxen.pattern;

import org.jaxen.Context;

public class AnyChildNodeTest extends NodeTest {
    private static AnyChildNodeTest instance = new AnyChildNodeTest();

    public static AnyChildNodeTest getInstance() {
        return instance;
    }

    public boolean matches(Object node, Context context) {
        short type = context.getNavigator().getNodeType(node);
        if (type == 1 || type == 3 || type == 8 || type == 7) {
            return true;
        }
        return false;
    }

    public double getPriority() {
        return -0.5d;
    }

    public short getMatchType() {
        return 0;
    }

    public String getText() {
        return "*";
    }
}
