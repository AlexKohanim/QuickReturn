package org.jaxen.pattern;

import org.jaxen.Context;

public class AnyNodeTest extends NodeTest {
    private static AnyNodeTest instance = new AnyNodeTest();

    public static AnyNodeTest getInstance() {
        return instance;
    }

    private AnyNodeTest() {
    }

    public boolean matches(Object node, Context context) {
        return true;
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
