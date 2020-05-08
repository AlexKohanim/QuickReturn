package org.jaxen.pattern;

import org.jaxen.Context;

public class NoNodeTest extends NodeTest {
    private static NoNodeTest instance = new NoNodeTest();

    public static NoNodeTest getInstance() {
        return instance;
    }

    public boolean matches(Object node, Context context) {
        return false;
    }

    public double getPriority() {
        return -0.5d;
    }

    public short getMatchType() {
        return 14;
    }

    public String getText() {
        return "";
    }
}
