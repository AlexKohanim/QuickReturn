package org.jaxen.javabean;

public class Element {
    private String name;
    private Object object;
    private Element parent;

    public Element(Element parent2, String name2, Object object2) {
        this.parent = parent2;
        this.name = name2;
        this.object = object2;
    }

    public Element getParent() {
        return this.parent;
    }

    public String getName() {
        return this.name;
    }

    public Object getObject() {
        return this.object;
    }
}
